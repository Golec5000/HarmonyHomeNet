'use client'

import React, {useState, useEffect} from 'react';
import {toast} from "sonner";
import {Button} from "@/components/ui/button";
import {Home, FileText, CreditCard, Bell, User, Vote, Settings, LogOut, BookUser} from 'lucide-react';
import {DropdownMenu, DropdownMenuContent, DropdownMenuItem, DropdownMenuTrigger} from "@/components/ui/dropdown-menu";
import {ModeToggle} from "@/components/ModeToggle";
import ApartmentCombobox from "@/components/owner-components/ApartmentCombobox";
import Link from 'next/link';
import {HomePage} from '@/components/owner-components/owner-main-page';
import Announcements from "@/components/owner-components/announcements";
import {DocumentsSection} from "@/components/owner-components/owner-documents";
import {jwtDecode} from "jwt-decode";
import {ContactAdmin} from "@/components/owner-components/contact-admin";

const navItems = [
    {label: 'Strona główna', icon: Home},
    {label: 'Ogłoszenia', icon: Bell},
    {label: 'Płatności', icon: CreditCard},
    {label: 'Dokumenty', icon: FileText},
    {label: 'Głosowania', icon: Vote},
    {label: 'Zgłoszenie problemu', icon: BookUser},
    {label: 'Ustawienia', icon: Settings},
];

export default function MainResidentsPage() {
    const [selectedItem, setSelectedItem] = useState(navItems[0].label);
    const [selectedApartment, setSelectedApartment] = useState<string | null>(null);

    useEffect(() => {
        const token = localStorage.getItem('jwt_accessToken');
        if (!token) {
            window.location.href = '/login';
            return;
        }

        try {
            const decodedToken = jwtDecode<{ exp: number }>(token);
            if (decodedToken.exp * 1000 < Date.now()) {
                localStorage.removeItem('jwt_accessToken');
                window.location.href = '/login';
            }
        } catch (error) {
            console.error('Error decoding token:', error);
            localStorage.removeItem('jwt_accessToken');
            window.location.href = '/login';
        }
    }, []);

    const handleSelectApartment = (value: string) => {
        toast("Apartment Selected", {
            description: `Selected apartment ID: ${value}`
        });
        setSelectedApartment(value);
    };

    const renderContent = () => {
        switch (selectedItem) {
            case 'Strona główna':
                return <HomePage/>;
            case 'Ogłoszenia':
                return <Announcements apartmentSignature={selectedApartment}/>;
            case 'Dokumenty':
                return <DocumentsSection/>;
            case 'Zgłoszenie problemu':
                return <ContactAdmin apartmentSignature={selectedApartment}/>;
            default:
                return <div>Content for {selectedItem}</div>;
        }
    };

    return (
        <div className="flex h-screen bg-background text-foreground">
            <nav className="w-64 bg-card border-r border-border">
                <div className="p-4 border-b border-border flex items-center space-x-2">
                    <Home className="h-6 w-6 text-primary"/>
                    <h1 className="text-xl font-bold">eBOK - HHN</h1>
                </div>
                <ul className="p-4 space-y-2">
                    {navItems.map((item) => (
                        <li key={item.label}>
                            <Button
                                variant={selectedItem === item.label ? "secondary" : "ghost"}
                                className="w-full justify-start"
                                onClick={() => setSelectedItem(item.label)}
                            >
                                <item.icon className="mr-2 h-4 w-4"/>
                                {item.label}
                            </Button>
                        </li>
                    ))}
                </ul>
            </nav>
            <div className="flex-1 flex flex-col">
                <header className="bg-card border-b border-border p-4 flex justify-between items-center">
                    <div className="flex-1 flex justify-center">
                        <ApartmentCombobox onSelect={handleSelectApartment}/>
                    </div>
                    <div className="flex items-center space-x-4">
                        <ModeToggle/>
                        <Button variant="secondary" size="icon">
                            <Bell className="h-5 w-5"/>
                        </Button>
                        <DropdownMenu>
                            <DropdownMenuTrigger asChild>
                                <Button variant="secondary" size="icon">
                                    <User className="h-5 w-5"/>
                                </Button>
                            </DropdownMenuTrigger>
                            <DropdownMenuContent>
                                <DropdownMenuItem asChild>
                                    <Link href="/welcome-home" onClick={async () => {
                                        try {
                                            const response = await fetch('http://localhost:8444/bwp/hhn/api/v1/logout', {
                                                method: 'POST',
                                                headers: {
                                                    'Authorization': `Bearer ${localStorage.getItem('jwt_accessToken')}`
                                                }
                                            });
                                            if (response.ok) {
                                                localStorage.removeItem('jwt_accessToken');
                                                window.location.href = '/welcome-home';
                                            } else {
                                                window.location.href = '/welcome-home';
                                            }
                                        } catch (error) {
                                            console.error('Error logging out:', error);
                                        }
                                    }}>
                                        <a className="flex items-center">
                                            <LogOut className="mr-2 h-4 w-4"/>
                                            Logout
                                        </a>
                                    </Link>
                                </DropdownMenuItem>
                            </DropdownMenuContent>
                        </DropdownMenu>
                    </div>
                </header>
                <main className="flex-1 overflow-y-auto p-8">
                    {renderContent()}
                </main>
            </div>
        </div>
    );
}