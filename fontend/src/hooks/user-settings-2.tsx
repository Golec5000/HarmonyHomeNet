'use client'

import React, {useEffect, useState} from 'react'
import {Card, CardContent, CardDescription, CardHeader, CardTitle} from "@/components/ui/card"
import {Input} from "@/components/ui/input"
import {Label} from "@/components/ui/label"
import {Button} from "@/components/ui/button"
import {toast} from "sonner"
import {Lock, Phone, Settings} from 'lucide-react'
import {z} from 'zod'
import {jwtDecode} from 'jwt-decode'

const passwordSchema = z.string().min(8, "Password must be at least 8 characters long")
const phoneSchema = z.string().regex(/^\d{9,11}$/, "Invalid phone number format")

interface CustomJwtPayload {
    userId: string;
    sub: string;
}

export function UserSettingsV2() {
    const [newPassword, setNewPassword] = useState('')
    const [confirmPassword, setConfirmPassword] = useState('')
    const [phoneNumber, setPhoneNumber] = useState('')
    const [isLoading, setIsLoading] = useState(false)

    useEffect(() => {
        const fetchUserData = async () => {
            const token = sessionStorage.getItem('jwt_accessToken')
            if (!token) {
                toast.error("User not authenticated")
                window.location.href = '/login'
                return
            }
            const decodedToken = jwtDecode<CustomJwtPayload>(token);
            const email = decodedToken.sub;
            try {
                const response = await fetch(`http://localhost:8444/bwp/hhn/api/v1/user/get-user-by-email/${email}`, {
                    headers: {
                        'Authorization': `Bearer ${token}`
                    }
                })
                if (response.ok) {
                    const data = await response.json()
                    setPhoneNumber(data.phoneNumber)
                } else {
                    toast.error("Failed to fetch user data")
                }
            } catch (error) {
                toast.error("Error fetching user data")
            }
        }

        fetchUserData()
    }, [])

    const handlePasswordChange = async (e: React.FormEvent) => {
        e.preventDefault()
        try {
            passwordSchema.parse(newPassword)
            passwordSchema.parse(confirmPassword)
            if (newPassword !== confirmPassword) {
                toast.error("Passwords do not match")
                return
            }
            setIsLoading(true)
            const token = sessionStorage.getItem('jwt_accessToken')
            if (!token) {
                toast.error("User not authenticated")
                window.location.href = '/login'
                return
            }
            const decodedToken = jwtDecode<{ email: string }>(token)

            const bodyRequest = JSON.stringify({
                newPassword,
                confirmPassword,
                email: decodedToken.email
            });

            const response = await fetch('http://localhost:8444/bwp/hhn/api/v1/user/change-password', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${token}`
                },
                body: bodyRequest
            })
            if (response.ok) {
                toast.success("Password changed successfully")
                setNewPassword('')
                setConfirmPassword('')
            } else {
                toast.error("Failed to change password")
            }
        } catch (error) {
            if (error instanceof z.ZodError) {
                toast.error(error.errors[0].message)
            } else {
                toast.error("Failed to change password")
            }
        } finally {
            setIsLoading(false)
        }
    }

    const handlePhoneNumberChange = async (e: React.FormEvent) => {
        e.preventDefault()
        try {
            phoneSchema.parse(phoneNumber)
            setIsLoading(true)
            const token = sessionStorage.getItem('jwt_accessToken')
            if (!token) {
                toast.error("User not authenticated")
                window.location.href = '/login'
                return
            }
            const decodedToken = jwtDecode<CustomJwtPayload>(token)
            const bodyRequest = JSON.stringify({
                phoneNumber
            });

            const response = await fetch(`http://localhost:8444/bwp/hhn/api/v1/user/update-user-by-id?userId=${decodedToken.userId}`, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${token}`
                },
                body: bodyRequest
            })
            if (response.ok) {
                toast.success("Phone number updated successfully")
            } else {
                toast.error("Failed to update phone number")
            }
        } catch (error) {
            if (error instanceof z.ZodError) {
                toast.error(error.errors[0].message)
            } else {
                toast.error("Failed to update phone number")
            }
        } finally {
            setIsLoading(false)
        }
    }

    return (
        <div className="space-y-6">
            <h1 className="text-3xl font-bold flex items-center">
                <Settings className="mr-2 h-8 w-8 text-primary"/>
                User Settings
            </h1>

            <Card>
                <CardHeader>
                    <CardTitle className="flex items-center">
                        <Lock className="mr-2 h-5 w-5"/>
                        Change Password
                    </CardTitle>
                    <CardDescription>Enter your new password twice to confirm</CardDescription>
                </CardHeader>
                <CardContent>
                    <form onSubmit={handlePasswordChange} className="space-y-4">
                        <div className="space-y-2">
                            <Label htmlFor="new-password">New Password</Label>
                            <Input
                                id="new-password"
                                type="password"
                                value={newPassword}
                                onChange={(e) => setNewPassword(e.target.value)}
                                required
                            />
                        </div>
                        <div className="space-y-2">
                            <Label htmlFor="confirm-password">Confirm New Password</Label>
                            <Input
                                id="confirm-password"
                                type="password"
                                value={confirmPassword}
                                onChange={(e) => setConfirmPassword(e.target.value)}
                                required
                            />
                        </div>
                        <Button type="submit" disabled={isLoading}>
                            {isLoading ? 'Changing...' : 'Change Password'}
                        </Button>
                    </form>
                </CardContent>
            </Card>

            <Card>
                <CardHeader>
                    <CardTitle className="flex items-center">
                        <Phone className="mr-2 h-5 w-5"/>
                        Phone Number
                    </CardTitle>
                    <CardDescription>Add or edit your phone number</CardDescription>
                </CardHeader>
                <CardContent>
                    <form onSubmit={handlePhoneNumberChange} className="space-y-4">
                        <div className="space-y-2">
                            <Label htmlFor="phone-number">Phone Number</Label>
                            <Input
                                id="phone-number"
                                type="tel"
                                value={phoneNumber}
                                onChange={(e) => setPhoneNumber(e.target.value)}
                                placeholder="+1 (123) 456-7890"
                                required
                            />
                        </div>
                        <Button type="submit" disabled={isLoading}>
                            {isLoading ? 'Updating...' : 'Update Phone Number'}
                        </Button>
                    </form>
                </CardContent>
            </Card>
        </div>
    )
}