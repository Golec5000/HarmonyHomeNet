'use client'

import React, { useState } from 'react'
import { toast } from "sonner"
import { Button } from "@/components/ui/button"
import { Home, FileText, CreditCard, Bell, User, Vote, Settings, LogOut, BookUser } from 'lucide-react'
import { HomePage } from "@/components/residents-components/home-page"
import { Alert404 } from "@/components/utils-components/404Error"
import ApartmentCombobox from "@/components/residents-components/ApartmentCombobox"
import { Announcements } from "@/components/residents-components/announcements-page"
import { Payments } from "@/components/residents-components/payments-page"
import { DropdownMenu, DropdownMenuContent, DropdownMenuItem, DropdownMenuTrigger } from "@/components/ui/dropdown-menu"
import { Voting } from "@/components/residents-components/vouting-page"
import { ContactAdmin } from "@/components/residents-components/contacts-page"
import { useRouter } from "next/navigation"
import {Documents} from "@/components/residents-components/documents-page";
import {ModeToggle} from "@/components/utils-components/theme-switch";

const navItems = [
    { label: 'Home', icon: Home },
    { label: 'Announcements', icon: Bell },
    { label: 'Payments', icon: CreditCard },
    { label: 'Documents', icon: FileText },
    { label: 'Voting', icon: Vote },
    { label: 'Contact', icon: BookUser },
]

export default function MainResidentsPage() {
    const router = useRouter()
    const [selectedItem, setSelectedItem] = useState(navItems[0].label)

    const handleSelectApartment = (value: string) => {
        console.log(`Selected apartment ID: ${value}`)
        toast("Apartment Selected", {
            description: `Selected apartment ID: ${value}`,
            action: {
                label: "Undo",
                onClick: () => console.log("Undo"),
            },
        })
    }

    const renderContent = () => {
        switch (selectedItem) {
            case 'Home':
                return <HomePage />
            case 'Documents':
                return <Documents />
            case 'Payments':
                return <Payments />
            case 'Announcements':
                return <Announcements />
            case 'Voting':
                return <Voting />
            case 'Contact':
                return <ContactAdmin />
            default:
                return <div><Alert404 /></div>
        }
    }

    return (
        <div className="flex h-screen bg-gray-100 dark:bg-gray-900">
            <nav className="w-64 bg-white dark:bg-gray-800 border-r dark:border-gray-700">
                <div className="p-4 border-b dark:border-gray-700 flex items-center space-x-2">
                    <Home className="h-6 w-6 text-blue-600 dark:text-blue-400" />
                    <h1 className="text-xl font-bold text-gray-800 dark:text-gray-100">eBOK - HHN</h1>
                </div>
                <ul className="p-4 space-y-2">
                    {navItems.map((item) => (
                        <li key={item.label}>
                            <Button
                                variant={selectedItem === item.label ? "secondary" : "ghost"}
                                className="w-full justify-start"
                                onClick={() => setSelectedItem(item.label)}
                            >
                                <item.icon className="mr-2 h-4 w-4 dark:text-gray-400" />
                                {item.label}
                            </Button>
                        </li>
                    ))}
                </ul>
            </nav>
            <div className="flex-1 flex flex-col">
                <header className="bg-white dark:bg-gray-800 border-b dark:border-gray-700 p-4 flex justify-between items-center">
                    <div className="flex-1 flex justify-center">
                        <ApartmentCombobox onSelect={handleSelectApartment} />
                    </div>
                    <div className="flex items-center space-x-4">
                        <ModeToggle />
                        <Button variant="secondary" size="icon">
                            <Bell className="h-5 w-5 dark:text-gray-400" />
                        </Button>
                        <Button variant="secondary" size="icon">
                            <DropdownMenu>
                                <DropdownMenuTrigger asChild>
                                    <User className="h-5 w-5 dark:text-gray-400" />
                                </DropdownMenuTrigger>
                                <DropdownMenuContent>
                                    <DropdownMenuItem onSelect={() => router.push('/home/residents/settings')}>
                                        <Settings className="mr-2 h-4 w-4 dark:text-gray-400" />
                                        Settings
                                    </DropdownMenuItem>
                                    <DropdownMenuItem onSelect={() => router.push('/home/welcome')}>
                                        <LogOut className="mr-2 h-4 w-4 dark:text-gray-400" />
                                        Logout
                                    </DropdownMenuItem>
                                </DropdownMenuContent>
                            </DropdownMenu>
                        </Button>
                    </div>
                </header>
                <main className="flex-1 overflow-y-auto p-8 dark:bg-gray-900 dark:text-gray-100">
                    {renderContent()}
                </main>
            </div>
        </div>
    )
}