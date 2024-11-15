import React from 'react'
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import {Home, Phone, Mail, Calendar, MessageSquare} from 'lucide-react'

export function HomePage() {
    return (
        <div className="space-y-6">
            <h1 className="text-3xl font-bold flex items-center">
                <Home className="mr-2 h-8 w-8 text-primary"/>
                Witamy w Harmony Home
            </h1>
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">

                <Card>
                    <CardHeader className="flex flex-row items-center space-x-2">
                        <Phone className="h-6 w-6 text-primary"/>
                        <CardTitle>Informacje kontaktowe</CardTitle>
                    </CardHeader>
                    <CardContent>
                        <ul className="space-y-2">
                            <li>Alarmowy: 112</li>
                            <li>Zarządca Nieruchomości: (555) 123-4567</li>
                            <li>Konserwacja: (555) 987-6543</li>
                        </ul>
                    </CardContent>
                </Card>

                <Card>
                    <CardHeader className="flex flex-row items-center space-x-2">
                        <Mail className="h-6 w-6 text-primary"/>
                        <CardTitle>Kontakt z administracją</CardTitle>
                    </CardHeader>
                    <CardContent>
                        <ul className="space-y-2">
                            <li>Zapytania ogólne: info@harmonyhomenet.com</li>
                            <li>Zgłoszenia konserwacyjne: maintenance@harmonyhomenet.com</li>
                            <li>Pytania dotyczące faktur: billing@harmonyhomenet.com</li>
                        </ul>
                    </CardContent>
                </Card>

                <Card>
                    <CardHeader className="flex flex-row items-center space-x-2">
                        <Calendar className="h-6 w-6 text-primary"/>
                        <CardTitle>Godziny otwarcia biura</CardTitle>
                    </CardHeader>
                    <CardContent>
                        <ul className="space-y-2">
                            <li>Poniedziałek - Piątek: 09:00 - 17:00</li>
                            <li>Sobota: 10:00 - 14:00</li>
                            <li>Niedziela: Zamknięte</li>
                        </ul>
                    </CardContent>
                </Card>
            </div>

            <Card>
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
    )
}
