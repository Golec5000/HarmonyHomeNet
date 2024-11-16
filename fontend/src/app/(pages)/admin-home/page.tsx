'use client'

import React, {useState, useEffect} from 'react';
import {toast} from "sonner";
import {Button} from "@/components/ui/button";
import {
    Home,
    FileText,
    CreditCard,
    Bell,
    User,
    Vote,
    Settings,
    LogOut,
    BookUser,
    Clock,
    Users,
    Briefcase,
    Building,
    PhoneCall,
    Mail
} from 'lucide-react';
import {DropdownMenu, DropdownMenuContent, DropdownMenuItem, DropdownMenuTrigger} from "@/components/ui/dropdown-menu";
import {ModeToggle} from "@/components/ModeToggle";
import Link from 'next/link';
import {jwtDecode} from "jwt-decode";
import {UserSettingsV2} from "@/hooks/user-settings-2";
import {UsersManagement} from "@/components/admin-components/users-management";
import {ApartmentManagement} from "@/components/admin-components/apartment-management";
import {AnnouncementManagement} from "@/components/admin-components/announcement-management";
import {PaymentManagement} from "@/components/admin-components/payment-management";
import {Card, CardHeader, CardTitle, CardDescription, CardContent} from "@/components/ui/card";
import {DocumentManagementComponent} from "@/components/admin-components/document-management";

const navItems = [
    {label: 'Dashboard', icon: Home},
    {label: 'Użytkownicy', icon: Users},
    {label: 'Mieszkania', icon: Building},
    {label: 'Ogłoszenia', icon: Bell},
    {label: 'Płatności', icon: CreditCard},
    {label: 'Dokumenty', icon: FileText},
    {label: 'Zgłoszenia', icon: BookUser},
    {label: 'Głosowania', icon: Vote},
    {label: 'Ustawienia', icon: Settings},
];

export default function EmployeeMainPageComponent() {
    const [selectedItem, setSelectedItem] = useState(navItems[0].label);
    const [remainingTime, setRemainingTime] = useState<string>('00:00');
    const [employeeName, setEmployeeName] = useState<string>('');

    useEffect(() => {
        const token = localStorage.getItem('jwt_accessToken');
        if (!token) {
            window.location.href = '/login';
            return;
        }

        try {
            const decodedToken = jwtDecode<{ exp: number; name: string }>(token);
            const expirationTime = decodedToken.exp * 1000;
            setEmployeeName(decodedToken.name);

            const updateRemainingTime = () => {
                const currentTime = Date.now();
                const timeLeft = expirationTime - currentTime;
                if (timeLeft <= 0) {
                    localStorage.removeItem('jwt_accessToken');
                    window.location.href = '/login';
                } else {
                    const minutes = Math.floor(timeLeft / 60000);
                    const seconds = Math.floor((timeLeft % 60000) / 1000);
                    setRemainingTime(`${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}`);
                }
            };

            updateRemainingTime();
            const intervalId = setInterval(updateRemainingTime, 1000);

            return () => clearInterval(intervalId);
        } catch (error) {
            console.error('Error decoding token:', error);
            localStorage.removeItem('jwt_accessToken');
            window.location.href = '/login';
        }
    }, []);

    const renderContent = () => {
        switch (selectedItem) {
            case 'Dashboard':
                return <EmployeePanelMain setSelectedItem={setSelectedItem}/>;
            case 'Użytkownicy':
                return <UsersManagement/>;
            case 'Mieszkania':
                return <ApartmentManagement/>;
            case 'Ogłoszenia':
                return <AnnouncementManagement/>;
            case 'Płatności':
                return <PaymentManagement/>;
            case 'Dokumenty':
                return <DocumentManagementComponent/>;
            case 'Zgłoszenia':
                return <div>Lista zgłoszeń od mieszkańców</div>;
            case 'Głosowania':
                return <div>Głosowania</div>;
            case 'Ustawienia':
                return <UserSettingsV2/>;
            default:
                return <div>Wybierz opcję z menu</div>;
        }
    };

    return (
        <div className="flex h-screen bg-background text-foreground">
            <nav className="w-64 bg-card border-r border-border">
                <div className="p-4 border-b border-border flex items-center space-x-2">
                    <Briefcase className="h-6 w-6 text-primary"/>
                    <h1 className="text-xl font-bold">eBOK - Panel Pracownika</h1>
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
                    <h2 className="text-2xl font-bold"></h2>
                    <div className="flex items-center space-x-4">
                        <div className="flex items-center space-x-2 text-sm font-medium">
                            <Clock className="h-4 w-4 text-primary"/>
                            <span>{remainingTime}</span>
                        </div>
                        <ModeToggle/>
                        <DropdownMenu>
                            <DropdownMenuTrigger asChild>
                                <Button variant="secondary" size="icon">
                                    <User className="h-5 w-5"/>
                                </Button>
                            </DropdownMenuTrigger>
                            <DropdownMenuContent>
                                <DropdownMenuItem asChild>
                                    <Link href="/welcome-home" legacyBehavior>
                                        <a className="flex items-center" onClick={async () => {
                                            try {
                                                const response = await fetch('http://localhost:8444/logout', {
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
                                                toast.error("Wystąpił błąd podczas wylogowywania");
                                            }
                                        }}>
                                            <LogOut className="mr-2 h-4 w-4"/>
                                            Wyloguj
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

function EmployeePanelMain({setSelectedItem}) {
    return (
        <div className="container mx-auto px-4 py-8">
            <h1 className="text-3xl font-bold mb-8 text-center flex items-center justify-center">
                <Briefcase className="h-6 w-6 text-primary"/>
                <span> Panel Pracownika </span>
            </h1>

            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6 mb-12">
                <FunctionCard
                    icon={<Users className="h-8 w-8"/>}
                    title="Obsługa Użytkowników"
                    description="Dodawanie, edycja i usuwanie kont użytkowników."
                    onClick={() => setSelectedItem('Użytkownicy')}
                />
                <FunctionCard
                    icon={<FileText className="h-8 w-8"/>}
                    title="Zarządzanie Dokumentami"
                    description="Dodawanie i usuwanie dokumentów w systemie."
                    onClick={() => setSelectedItem('Dokumenty')}
                />
                <FunctionCard
                    icon={<CreditCard className="h-8 w-8"/>}
                    title="Płatności"
                    description="Tworzenie, edycja i usuwanie płatności."
                    onClick={() => setSelectedItem('Płatności')}
                />
                <FunctionCard
                    icon={<Vote className="h-8 w-8"/>}
                    title="Zarządzanie Głosowaniami"
                    description="Pełna obsługa systemu głosowań."
                    onClick={() => setSelectedItem('Głosowania')}
                />
                <FunctionCard
                    icon={<Building className="h-8 w-8"/>}
                    title="Zarządzanie Mieszkaniami"
                    description="Kompleksowe zarządzanie informacjami o mieszkaniach."
                    onClick={() => setSelectedItem('Mieszkania')}
                />
                <FunctionCard
                    icon={<Bell className="h-8 w-8"/>}
                    title="Zarządzanie Ogłoszeniami"
                    description="Publikowanie i zarządzanie ogłoszeniami dla mieszkańców."
                    onClick={() => setSelectedItem('Ogłoszenia')}
                />
                <FunctionCard
                    icon={<BookUser className="h-8 w-8"/>}
                    title="Zarządzanie zgłoszeniami"
                    description="Przeglądanie i zarządzanie zgłoszeniami od mieszkańców."
                    onClick={() => setSelectedItem('Zgłoszenia')}
                />
                <FunctionCard
                    icon={<Settings className="h-8 w-8"/>}
                    title="Ustawienia"
                    description="Zarządzanie ustawieniami konta i aplikacji."
                    onClick={() => setSelectedItem('Ustawienia')}
                />
            </div>

            <Card>
                <CardHeader>
                    <CardTitle>Kontakt z Administracją</CardTitle>
                    <CardDescription>W razie pytań lub problemów, skontaktuj się z administracją:</CardDescription>
                </CardHeader>
                <CardContent>
                    <div className="flex flex-col space-y-2">
                        <div className="flex items-center">
                            <PhoneCall className="h-5 w-5 mr-2"/>
                            <span>Telefon: +48 123 456 789</span>
                        </div>
                        <div className="flex items-center">
                            <Mail className="h-5 w-5 mr-2"/>
                            <span>Email: administracja@example.com</span>
                        </div>
                    </div>
                </CardContent>
            </Card>
        </div>
    );
}

function FunctionCard({icon, title, description, onClick}) {
    return (
        <Card>
            <CardHeader>
                <CardTitle className="flex items-center">
                {icon}
                    <span className="ml-2">{title}</span>
                </CardTitle>
                <CardDescription>{description}</CardDescription>
            </CardHeader>
            <CardContent>
                <Button className="w-full" onClick={onClick}>Przejdź do funkcji</Button>
            </CardContent>
        </Card>
    );
}