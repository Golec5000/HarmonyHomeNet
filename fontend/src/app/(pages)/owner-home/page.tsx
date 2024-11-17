'use client'

import React, {useEffect, useState} from 'react';
import {toast} from "sonner";
import {Button} from "@/components/ui/button";
import {
    Bell,
    BookUser,
    Calendar,
    Clock,
    CreditCard,
    FileText,
    Gauge,
    Home,
    LogOut,
    Mail,
    Phone,
    Settings,
    User,
    Vote
} from 'lucide-react';
import {DropdownMenu, DropdownMenuContent, DropdownMenuItem, DropdownMenuTrigger} from "@/components/ui/dropdown-menu";
import {ModeToggle} from "@/components/ModeToggle";
import ApartmentCombobox from "@/components/owner-components/ApartmentCombobox";
import Link from 'next/link';
import {Announcements} from "@/components/owner-components/announcements";
import {DocumentsSection} from "@/components/owner-components/owner-documents";
import {jwtDecode} from "jwt-decode";
import {ContactAdmin} from "@/components/owner-components/contact-admin";
import {Payments} from "@/components/owner-components/payments";
import {UserSettings} from "@/hooks/user-settings";
import {Card, CardContent, CardDescription, CardHeader, CardTitle} from "@/components/ui/card";

const navItems = [
    {label: 'Strona główna', icon: Home},
    {label: 'Ogłoszenia', icon: Bell},
    {label: 'Płatności', icon: CreditCard},
    {label: 'Dokumenty', icon: FileText},
    {label: 'Głosowania', icon: Vote},
    {label: 'Zgłoszenie problemu', icon: BookUser},
    {label: 'Stany liczników', icon: Gauge},
    {label: 'Ustawienia', icon: Settings},
];

const functionCards = [
    {icon: Bell, title: "Ogłoszenia", description: "Przeglądaj aktualne ogłoszenia i komunikaty."},
    {icon: CreditCard, title: "Płatności", description: "Zarządzaj swoimi płatnościami i fakturami."},
    {icon: FileText, title: "Dokumenty", description: "Dostęp do ważnych dokumentów i umów."},
    {icon: Vote, title: "Głosowania", description: "Uczestnictwo w głosowaniach wspólnoty."},
    {icon: BookUser, title: "Zgłoszenie problemu", description: "Zgłoś problem lub usterkę w mieszkaniu."},
    {icon: Gauge, title: "Stany liczników", description: "Wprowadź i przeglądaj stany liczników."},
    {icon: Settings, title: "Ustawienia", description: "Zarządzaj swoim kontem i ustawieniami."}
]


interface FunctionCardProps {
    icon: React.ReactNode;
    title: string;
    description: string;
    onClick: () => void;
}

interface ContactCardProps {
    icon: React.ReactNode;
    title: string;
    items: string[];
}

export default function MainResidentsPage() {
    const [selectedItem, setSelectedItem] = useState(navItems[0].label);
    const [selectedApartment, setSelectedApartment] = useState<string | null>(null);
    const [remainingTime, setRemainingTime] = useState<string>('00:00');

    useEffect(() => {
        const token = localStorage.getItem('jwt_accessToken');
        if (!token) {
            window.location.href = '/login';
            return;
        }

        try {
            const decodedToken = jwtDecode<{ exp: number }>(token);
            const expirationTime = decodedToken.exp * 1000;
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

    const handleSelectApartment = (value: string) => {
        toast("Apartment Selected", {
            description: `Selected apartment ID: ${value}`
        });
        setSelectedApartment(value);
    };

    const renderContent = () => {
        switch (selectedItem) {
            case 'Strona główna':
                return <HomePage setSelectedItem={setSelectedItem}/>;
            case 'Ogłoszenia':
                return <Announcements apartmentSignature={selectedApartment}/>;
            case 'Dokumenty':
                return <DocumentsSection/>;
            case 'Zgłoszenie problemu':
                return <ContactAdmin apartmentSignature={selectedApartment}/>;
            case 'Płatności':
                return <Payments apartmentSignature={selectedApartment}/>;
            case 'Ustawienia':
                return <UserSettings/>;
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
                                            }
                                        }}>
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

function HomePage({setSelectedItem}: { setSelectedItem: (item: string) => void }) {
    return (
        <div className="container mx-auto px-4 py-8">
            <h1 className="text-3xl font-bold mb-8 text-center flex items-center justify-center">
                <Home className="mr-2 h-8 w-8 text-primary"/>
                Panel Mieszkańca
            </h1>

            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6 mb-12">
                {functionCards.map((card, index) => (
                    <FunctionCard
                        key={index}
                        icon={<card.icon className="h-8 w-8"/>}
                        title={card.title}
                        description={card.description}
                        onClick={() => setSelectedItem(card.title)}
                    />
                ))}
            </div>

            <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
                <ContactCard
                    icon={<Phone className="h-6 w-6"/>}
                    title="Informacje kontaktowe"
                    items={[
                        "Alarmowy: 112",
                        "Zarządca Nieruchomości: (555) 123-4567",
                        "Konserwacja: (555) 987-6543"
                    ]}
                />
                <ContactCard
                    icon={<Mail className="h-6 w-6"/>}
                    title="Kontakt z administracją"
                    items={[
                        "Zapytania ogólne: info@harmonyhomenet.com",
                        "Zgłoszenia konserwacyjne: maintenance@harmonyhomenet.com",
                        "Pytania dotyczące faktur: billing@harmonyhomenet.com"
                    ]}
                />
                <ContactCard
                    icon={<Calendar className="h-6 w-6"/>}
                    title="Godziny otwarcia biura"
                    items={[
                        "Poniedziałek - Piątek: 09:00 - 17:00",
                        "Sobota: 10:00 - 14:00",
                        "Niedziela: Zamknięte"
                    ]}
                />
            </div>

            <Card className="mt-6">
                <CardHeader>
                    <CardTitle>O Harmony Home Net</CardTitle>
                </CardHeader>
                <CardContent>
                    <p>
                        Harmony Home Net to kompleksowy system zarządzania nieruchomościami
                        mieszkalnymi. Staramy się tworzyć harmonijne środowisko życia dla
                        wszystkich naszych mieszkańców, zapewniając łatwy dostęp do ważnych
                        informacji, usprawnione kanały komunikacji oraz efektywne zarządzanie
                        potrzebami mieszkańców.
                    </p>
                </CardContent>
            </Card>
        </div>
    );
}

function FunctionCard({ icon, title, description, onClick }: FunctionCardProps) {
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

function ContactCard({ icon, title, items }: ContactCardProps) {
    return (
        <Card>
            <CardHeader className="flex flex-row items-center space-x-2">
                {icon}
                <CardTitle>{title}</CardTitle>
            </CardHeader>
            <CardContent>
                <ul className="space-y-2">
                    {items.map((item, index) => (
                        <li key={index}>{item}</li>
                    ))}
                </ul>
            </CardContent>
        </Card>
    );
}