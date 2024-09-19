import React, {useState} from "react"
import {Check, ChevronsUpDown} from "lucide-react"
import {cn} from "@/lib/utils"
import {Button} from "@/components/ui/button"
import {
    Command,
    CommandEmpty,
    CommandGroup,
    CommandInput,
    CommandItem,
    CommandList,
} from "@/components/ui/command"
import {
    Popover,
    PopoverContent,
    PopoverTrigger,
} from "@/components/ui/popover"

const apartments = [
    {label: "Apartment 101", value: "101"},
    {label: "Apartment 102", value: "102"},
    {label: "Apartment 201", value: "201"},
    {label: "Apartment 202", value: "202"},
    {label: "Apartment 301", value: "301"},
    {label: "Apartment 302", value: "302"},
] as const

interface ApartmentComboboxProps {
    onSelect: (value: string) => void
}

export default function ApartmentCombobox({onSelect}: ApartmentComboboxProps) {
    const [selectedApartment, setSelectedApartment] = useState<string | undefined>()
    const [open, setOpen] = useState(false)

    const handleSelect = (value: string) => {
        setSelectedApartment(value)
        setOpen(false)
        onSelect(value)
    }

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
    )
}