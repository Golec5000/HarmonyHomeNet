import React from 'react'
import Link from 'next/link'
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Button } from "@/components/ui/button"
import { Home, FileText, CreditCard, Bell, MessageSquare, Vote, LogOut } from 'lucide-react'
import { ModeToggle } from "@/components/utils-components/theme-switch"

const features = [
    {
        icon: FileText,
        title: 'Document Management',
        description: 'Access and manage all your important documents in one place.'
    },
    {
        icon: CreditCard,
        title: 'Easy Payments',
        description: 'Pay your rent and other fees securely through our platform.'
    },
    {
        icon: Bell,
        title: 'Notifications',
        description: 'Stay updated with important announcements and reminders.'
    },
    {
        icon: MessageSquare,
        title: 'Maintenance Requests',
        description: 'Submit and track maintenance requests easily.'
    },
    {
        icon: Vote,
        title: 'Community Voting',
        description: 'Participate in community decisions through our voting system.'
    }
]

export default function Welcome() {
    return (
        <div className="min-h-screen bg-gray-100 dark:bg-gray-900 py-12 px-4 sm:px-6 lg:px-8 relative">
            <div className="absolute top-4 right-4">
                <ModeToggle />
            </div>
            <div className="max-w-4xl mx-auto">
                <div className="text-center mb-12">
                    <Home className="mx-auto h-16 w-16 text-blue-600 dark:text-blue-400"/>
                    <h1 className="mt-4 text-4xl font-extrabold text-gray-900 dark:text-gray-100 sm:text-5xl sm:tracking-tight lg:text-6xl">
                        Welcome to eBOK
                    </h1>
                    <p className="mt-4 max-w-2xl text-xl text-gray-500 dark:text-gray-300 mx-auto">
                        Your comprehensive residential property management system
                    </p>
                </div>

                <div className="flex justify-center my-8">
                    <Link href="/login">
                        <Button size="lg" className="flex items-center space-x-2">
                            <span>Go to login page</span>
                            <LogOut className="h-4 w-4"/>
                        </Button>
                    </Link>
                </div>

                <Card className="mb-8">
                    <CardHeader>
                        <CardTitle>Getting Started</CardTitle>
                    </CardHeader>
                    <CardContent>
                        <p className="text-gray-600 dark:text-gray-400 mb-4">
                            eBOK is designed to make your residential experience smoother and more convenient.
                            Here&apos;s what you can do with our platform:
                        </p>
                        <ul className="list-disc pl-5 space-y-2 text-gray-600 dark:text-gray-400">
                            <li>Access and manage your important documents</li>
                            <li>Make secure online payments for rent and fees</li>
                            <li>Submit and track maintenance requests</li>
                            <li>Stay informed with community announcements</li>
                            <li>Participate in community decisions through voting</li>
                        </ul>
                    </CardContent>
                </Card>

                <div className="grid grid-cols-1 md:grid-cols-2 gap-6 mb-12">
                    {features.map((feature, index) => (
                        <Card key={index}>
                            <CardHeader>
                                <CardTitle className="flex items-center">
                                    <feature.icon className="h-6 w-6 text-blue-600 dark:text-blue-400 mr-2"/>
                                    {feature.title}
                                </CardTitle>
                            </CardHeader>
                            <CardContent>
                                <p className="text-gray-600 dark:text-gray-400">{feature.description}</p>
                            </CardContent>
                        </Card>
                    ))}
                </div>
            </div>
        </div>
    )
}