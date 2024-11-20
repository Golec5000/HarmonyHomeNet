import React, {useEffect, useState} from "react";
import {Check, ChevronsUpDown} from "lucide-react";
import {cn} from "@/lib/utils";
import {Button} from "@/components/ui/button";
import {Command, CommandEmpty, CommandGroup, CommandInput, CommandItem, CommandList,} from "@/components/ui/command";
import {Popover, PopoverContent, PopoverTrigger,} from "@/components/ui/popover";

import {jwtDecode} from 'jwt-decode';

interface ApartmentComboboxProps {
    onSelect: (value: string) => void;
}

export default function ApartmentCombobox({onSelect}: ApartmentComboboxProps) {
    const [apartments, setApartments] = useState<{ label: string; value: string }[]>([]);
    const [selectedApartment, setSelectedApartment] = useState<string | undefined>();
    const [open, setOpen] = useState(false);

    const fetchApartmentsByUserId = async (userId: string) => {
        try {
            const response = await fetch(`http://localhost:8444/bwp/hhn/api/v1/apartment/get-all-user-apartments?userId=${userId}`, {
                headers: {
                    'Authorization': `Bearer ${sessionStorage.getItem('jwt_accessToken')}`,
                },
            });

            if (response.ok) {
                const data = await response.json();
                return data || []; // Ensure data.content is an array
            } else if (response.status === 401 || response.status === 403) {
                window.location.href = '/login';
            } else {
                console.error('Failed to fetch apartments:', response.statusText);
            }

        } catch (error) {
            console.error('Failed to fetch apartments:', error);
        }
    };

    const getUserIdFromToken = (token: string): string => {
        const decodedToken: { userId: string } = jwtDecode(token);
        return decodedToken.userId;
    };

    useEffect(() => {
        const fetchApartments = async () => {
            const token = sessionStorage.getItem('jwt_accessToken');
            if (token) {
                try {
                    const apartments = await fetchApartmentsByUserId(getUserIdFromToken(token));
                    setApartments(apartments.map((apartment: any) => ({
                        label: apartment.address + ', ' + apartment.city,
                        value: apartment.apartmentSignature,
                    })));
                } catch (error) {
                    console.error('Failed to fetch apartments:', error);
                }
            }
        };

        fetchApartments();
    }, []);

    const handleSelect = (value: string) => {
        setSelectedApartment(value);
        setOpen(false);
        onSelect(value);
    };

    return (
        <div className="flex space-x-4 items-center">
            <Popover open={open} onOpenChange={setOpen}>
                <PopoverTrigger asChild>
                    <Button
                        variant="outline"
                        role="combobox"
                        aria-expanded={open}
                        className="w-[200px] justify-between"
                    >
                        {selectedApartment
                            ? apartments.find((apartment) => apartment.value === selectedApartment)?.label
                            : "Select apartment"}
                        <ChevronsUpDown className="ml-2 h-4 w-4 shrink-0 opacity-50"/>
                    </Button>
                </PopoverTrigger>
                <PopoverContent className="w-[200px] p-0">
                    <Command>
                        <CommandInput placeholder="Search apartment..."/>
                        <CommandList>
                            <CommandEmpty>No apartment found.</CommandEmpty>
                            <CommandGroup>
                                {apartments.map((apartment) => (
                                    <CommandItem
                                        key={apartment.value}
                                        value={apartment.value}
                                        onSelect={handleSelect}
                                    >
                                        <Check
                                            className={cn(
                                                "mr-2 h-4 w-4",
                                                selectedApartment === apartment.value ? "opacity-100" : "opacity-0"
                                            )}
                                        />
                                        {apartment.label}
                                    </CommandItem>
                                ))}
                            </CommandGroup>
                        </CommandList>
                    </Command>
                </PopoverContent>
            </Popover>
        </div>
    );
}