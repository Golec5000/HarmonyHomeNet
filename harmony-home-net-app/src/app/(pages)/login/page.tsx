'use client'

import React, { useState } from 'react'
import { useRouter } from 'next/navigation'
import { Card, CardContent, CardDescription, CardFooter, CardHeader, CardTitle } from "@/components/ui/card"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs"
import { Home, User, ShieldCheck, ArrowRight } from 'lucide-react'
import Link from 'next/link'
import { ModeToggle } from "@/components/utils-components/theme-switch"

export default function LoginPage() {
    const [residentEmail, setResidentEmail] = useState('')
    const [residentPassword, setResidentPassword] = useState('')
    const [adminEmail, setAdminEmail] = useState('')
    const [adminPassword, setAdminPassword] = useState('')
    const router = useRouter()

    const handleResidentSignIn = (e: React.FormEvent) => {
        e.preventDefault()
        console.log('Resident sign-in:', { residentEmail, residentPassword })
        router.push('/home/residents')
    }

    const handleAdminSignIn = (e: React.FormEvent) => {
        e.preventDefault()
        console.log('Admin sign-in:', { adminEmail, adminPassword })
    }

    return (
        <div className="min-h-screen flex items-center justify-center bg-gray-100 dark:bg-gray-900 relative">
            <div className="absolute top-4 right-4">
                <ModeToggle />
            </div>
            <div className="max-w-4xl w-full px-4">
                <Card className="w-full">
                    <CardHeader className="text-center">
                        <div className="flex justify-center mb-4">
                            <Home className="h-12 w-12 text-blue-600 dark:text-blue-400" />
                        </div>
                        <CardTitle className="text-3xl font-bold text-gray-900 dark:text-gray-100">Welcome to Harmony Home Net eBOK</CardTitle>
                        <CardDescription className="text-gray-600 dark:text-gray-400">
                            Your Residential Property Management System
                        </CardDescription>
                    </CardHeader>
                    <CardContent>
                        <Tabs defaultValue="resident" className="w-full">
                            <TabsList className="grid w-full grid-cols-2">
                                <TabsTrigger value="resident">
                                    <User className="mr-2 h-4 w-4" />
                                    Resident
                                </TabsTrigger>
                                <TabsTrigger value="admin">
                                    <ShieldCheck className="mr-2 h-4 w-4" />
                                    Administrator
                                </TabsTrigger>
                            </TabsList>
                            <TabsContent value="resident">
                                <form onSubmit={handleResidentSignIn} className="space-y-4">
                                    <div className="space-y-2">
                                        <Label htmlFor="residentEmail">Email</Label>
                                        <Input
                                            id="residentEmail"
                                            type="email"
                                            placeholder="your.email@example.com"
                                            value={residentEmail}
                                            onChange={(e) => setResidentEmail(e.target.value)}
                                            required
                                        />
                                    </div>
                                    <div className="space-y-2">
                                        <Label htmlFor="residentPassword">Password</Label>
                                        <Input
                                            id="residentPassword"
                                            type="password"
                                            value={residentPassword}
                                            onChange={(e) => setResidentPassword(e.target.value)}
                                            required
                                        />
                                    </div>
                                    <Button type="submit" className="w-full">
                                        Sign In as Resident
                                        <ArrowRight className="ml-2 h-4 w-4" />
                                    </Button>
                                </form>
                            </TabsContent>
                            <TabsContent value="admin">
                                <form onSubmit={handleAdminSignIn} className="space-y-4">
                                    <div className="space-y-2">
                                        <Label htmlFor="adminEmail">Email</Label>
                                        <Input
                                            id="adminEmail"
                                            type="email"
                                            placeholder="admin@ebok.com"
                                            value={adminEmail}
                                            onChange={(e) => setAdminEmail(e.target.value)}
                                            required
                                        />
                                    </div>
                                    <div className="space-y-2">
                                        <Label htmlFor="adminPassword">Password</Label>
                                        <Input
                                            id="adminPassword"
                                            type="password"
                                            value={adminPassword}
                                            onChange={(e) => setAdminPassword(e.target.value)}
                                            required
                                        />
                                    </div>
                                    <Button type="submit" className="w-full">
                                        Sign In as Administrator
                                        <ArrowRight className="ml-2 h-4 w-4" />
                                    </Button>
                                </form>
                            </TabsContent>
                        </Tabs>
                    </CardContent>
                    <CardFooter className="flex justify-between">
                        <Link href="/forgot-password" className="text-sm text-blue-600 dark:text-blue-400 hover:underline">
                            Forgot password?
                        </Link>
                        <Link href="/register" className="text-sm text-blue-600 dark:text-blue-400 hover:underline">
                            New resident? Register here
                        </Link>
                    </CardFooter>
                </Card>
            </div>
        </div>
    )
}