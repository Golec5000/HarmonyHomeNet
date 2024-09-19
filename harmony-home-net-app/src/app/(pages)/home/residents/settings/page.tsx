'use client'

import React, {useState} from 'react'
import {useRouter} from 'next/navigation'
import {Card, CardContent, CardHeader, CardTitle} from "@/components/ui/card"
import {Button} from "@/components/ui/button"
import {Input} from "@/components/ui/input"
import {Label} from "@/components/ui/label"
import {Switch} from "@/components/ui/switch"
import {Select, SelectContent, SelectItem, SelectTrigger, SelectValue} from "@/components/ui/select"
import {Tabs, TabsContent, TabsList, TabsTrigger} from "@/components/ui/tabs"
import {UserCog, User, Bell, Shield, Home} from 'lucide-react'
import {ModeToggle} from "@/components/utils-components/theme-switch";

export default function Settings() {
    const [notificationsEnabled, setNotificationsEnabled] = useState(true)
    const [twoFactorEnabled, setTwoFactorEnabled] = useState(false)
    const router = useRouter()

    return (
        <div className="h-screen w-full py-6 space-y-6 bg-gray-100 dark:bg-gray-900 text-gray-800 dark:text-gray-200">
            <div className="flex justify-between items-center">
                <h1 className="text-3xl font-bold text-gray-800 dark:text-gray-100 flex items-center">
                    <UserCog className="mr-2 h-8 w-8 text-gray-600 dark:text-gray-400"/>
                    Settings
                </h1>
                <div className="flex space-x-4">
                    <Button variant="outline" onClick={() => router.push('/home/residents')}>
                        <Home className="mr-2 h-4 w-4"/>
                        Back to Home
                    </Button>
                    <ModeToggle/>
                </div>
            </div>

            <Tabs defaultValue="account" className="space-y-4">
                <TabsList className="bg-white dark:bg-gray-800">
                    <TabsTrigger value="account">Account</TabsTrigger>
                    <TabsTrigger value="notifications">Notifications</TabsTrigger>
                    <TabsTrigger value="security">Security</TabsTrigger>
                </TabsList>

                <TabsContent value="account">
                    <Card className="bg-white dark:bg-gray-800 text-gray-900 dark:text-gray-100">
                        <CardHeader>
                            <CardTitle className="flex items-center">
                                <User className="mr-2 h-5 w-5"/>
                                Account Information
                            </CardTitle>
                        </CardHeader>
                        <CardContent className="space-y-4">
                            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                                <div className="space-y-2">
                                    <Label htmlFor="firstName">First Name</Label>
                                    <Input id="firstName" placeholder="John"/>
                                </div>
                                <div className="space-y-2">
                                    <Label htmlFor="lastName">Last Name</Label>
                                    <Input id="lastName" placeholder="Doe"/>
                                </div>
                            </div>
                            <div className="space-y-2">
                                <Label htmlFor="email">Email</Label>
                                <Input id="email" type="email" placeholder="john.doe@example.com"/>
                            </div>
                            <div className="space-y-2">
                                <Label htmlFor="phone">Phone Number</Label>
                                <Input id="phone" type="tel" placeholder="+1 (555) 123-4567"/>
                            </div>
                            <Button>Save Changes</Button>
                        </CardContent>
                    </Card>
                </TabsContent>

                <TabsContent value="notifications">
                    <Card className="bg-white dark:bg-gray-800 text-gray-900 dark:text-gray-100">
                        <CardHeader>
                            <CardTitle className="flex items-center">
                                <Bell className="mr-2 h-5 w-5"/>
                                Notification Preferences
                            </CardTitle>
                        </CardHeader>
                        <CardContent className="space-y-4">
                            <div className="flex items-center justify-between">
                                <Label htmlFor="notifications">Enable Notifications</Label>
                                <Switch
                                    id="notifications"
                                    checked={notificationsEnabled}
                                    onCheckedChange={setNotificationsEnabled}
                                />
                            </div>
                            {notificationsEnabled && (
                                <>
                                    <div className="space-y-2">
                                        <Label htmlFor="notificationMethod">Preferred Notification Method</Label>
                                        <Select>
                                            <SelectTrigger id="notificationMethod">
                                                <SelectValue placeholder="Select a method"/>
                                            </SelectTrigger>
                                            <SelectContent>
                                                <SelectItem value="email">Email</SelectItem>
                                                <SelectItem value="sms">SMS</SelectItem>
                                            </SelectContent>
                                        </Select>
                                    </div>
                                    <div className="space-y-2">
                                        <Label>Notification Types</Label>
                                        <div className="space-y-2">
                                            <div className="flex items-center space-x-2">
                                                <Switch id="paymentReminders"/>
                                                <Label htmlFor="paymentReminders">Payment Reminders</Label>
                                            </div>
                                            <div className="flex items-center space-x-2">
                                                <Switch id="maintenanceUpdates"/>
                                                <Label htmlFor="maintenanceUpdates">Maintenance Updates</Label>
                                            </div>
                                            <div className="flex items-center space-x-2">
                                                <Switch id="communityAnnouncements"/>
                                                <Label htmlFor="communityAnnouncements">Community Announcements</Label>
                                            </div>
                                        </div>
                                    </div>
                                </>
                            )}
                            <Button>Save Preferences</Button>
                        </CardContent>
                    </Card>
                </TabsContent>

                <TabsContent value="security">
                    <Card className="bg-white dark:bg-gray-800 text-gray-900 dark:text-gray-100">
                        <CardHeader>
                            <CardTitle className="flex items-center">
                                <Shield className="mr-2 h-5 w-5"/>
                                Security Settings
                            </CardTitle>
                        </CardHeader>
                        <CardContent className="space-y-4">
                            <div className="space-y-2">
                                <Label htmlFor="currentPassword">Current Password</Label>
                                <Input id="currentPassword" type="password"/>
                            </div>
                            <div className="space-y-2">
                                <Label htmlFor="newPassword">New Password</Label>
                                <Input id="newPassword" type="password"/>
                            </div>
                            <div className="space-y-2">
                                <Label htmlFor="confirmPassword">Confirm New Password</Label>
                                <Input id="confirmPassword" type="password"/>
                            </div>
                            <div className="flex items-center justify-between">
                                <Label htmlFor="twoFactor">Enable Two-Factor Authentication</Label>
                                <Switch
                                    id="twoFactor"
                                    checked={twoFactorEnabled}
                                    onCheckedChange={setTwoFactorEnabled}
                                />
                            </div>
                            {twoFactorEnabled && (
                                <div className="space-y-2">
                                    <Label htmlFor="twoFactorMethod">Two-Factor Method</Label>
                                    <Select>
                                        <SelectTrigger id="twoFactorMethod">
                                            <SelectValue placeholder="Select a method"/>
                                        </SelectTrigger>
                                        <SelectContent>
                                            <SelectItem value="app">Authenticator App</SelectItem>
                                            <SelectItem value="sms">SMS</SelectItem>
                                            <SelectItem value="email">Email</SelectItem>
                                        </SelectContent>
                                    </Select>
                                </div>
                            )}
                            <Button>Update Security Settings</Button>
                        </CardContent>
                    </Card>
                </TabsContent>
            </Tabs>
        </div>
    )
}