'use client'

import React, {useState} from 'react'
import {Card, CardContent, CardDescription, CardHeader, CardTitle} from "@/components/ui/card"
import {Input} from "@/components/ui/input"
import {Button} from "@/components/ui/button"
import {Label} from "@/components/ui/label"
import {toast} from "sonner"
import {ModeToggle} from "@/components/ModeToggle";
import {Key, Lock, Mail, Send} from 'lucide-react'
import {string, z} from 'zod'

export default function PasswordReset() {
    const [email, setEmail] = useState('')
    const [newPassword, setNewPassword] = useState('')
    const [confirmPassword, setConfirmPassword] = useState('')
    const [resetToken, setResetToken] = useState('')

    const emailSchema = z.object({
        email: z.string().email("Invalid email address")
    });

    const handleSendResetCode = async (e: React.FormEvent) => {
        e.preventDefault()
        try {

            const body = emailSchema.parse({email})

            const response = await fetch('http://localhost:8444/bwp/hhn/api/v1/auth/forgot-password', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(body)
            })

            if (response.ok) {
                toast.success(`Password reset link has been sent to ${email}`)
                setEmail('')
            } else {
                toast.error("Failed to send reset link. Please try again.")
            }

        } catch (error) {
            toast.error("Failed to send reset link. Please try again.")
        }
    }

    const passwordSchema = z.object({
        newPassword: z.string().min(8, "Password must contain at least 8 characters"),
        confirmPassword: z.string().min(8, "Password must contain at least 8 characters"),
        token: string()
    }).refine(data => data.newPassword === data.confirmPassword, {
        message: "Passwords do not match",
        path: ["confirmPassword"],
    });

    const handleResetPassword = async (e: React.FormEvent) => {
        e.preventDefault()
        if (newPassword !== confirmPassword) {
            toast.error("Passwords do not match")
            return
        }
        try {

            const body = passwordSchema.parse({newPassword, confirmPassword, token: resetToken})

            const response = await fetch('http://localhost:8444/bwp/hhn/api/v1/auth/reset-password', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(body)
            })

            if (response.ok) {
                setNewPassword('')
                setConfirmPassword('')
                setResetToken('')
                toast.success("Password has been reset successfully")
                window.location.href = '/login'
            } else {
                setNewPassword('')
                setConfirmPassword('')
                setResetToken('')
                toast.error("Failed to reset password. Please try again.")
            }

        } catch (error) {
            toast.error("Failed to reset password. Please try again.")
        }
    }

    return (
        <div className="container mx-auto p-4">
            <div className="flex justify-end mb-4">
                <ModeToggle/>
            </div>
            <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                <Card>
                    <CardHeader>
                        <CardTitle>
                            <Send className="mr-2 h-6 w-6"/>
                            Wyślij token resetujący
                        </CardTitle>
                        <CardDescription>Wprowadź swój email, aby otrzymać token do resetowania hasła</CardDescription>
                    </CardHeader>
                    <CardContent>
                        <form onSubmit={handleSendResetCode} className="space-y-4">
                            <div className="space-y-2">
                                <Label htmlFor="email" className="flex items-center">
                                    <Mail className="mr-2 h-4 w-4"/>
                                    Email
                                </Label>
                                <Input
                                    id="email"
                                    type="email"
                                    placeholder="Wprowadź swój email"
                                    value={email}
                                    onChange={(e) => setEmail(e.target.value)}
                                    required
                                />
                            </div>
                            <Button type="submit" className="w-full">
                                <Send className="mr-2 h-4 w-4"/>
                                Wyślij token resetujący
                            </Button>
                        </form>
                    </CardContent>
                </Card>

                <Card>
                    <CardHeader>
                        <CardTitle className="flex items-center">
                            <Key className="mr-2 h-6 w-6"/>
                            Zresetuj hasło
                        </CardTitle>
                        <CardDescription>Wprowadź swoje nowe hasło i token resetujący</CardDescription>
                    </CardHeader>
                    <CardContent>
                        <form onSubmit={handleResetPassword} className="space-y-4">
                            <div className="space-y-2">
                                <Label htmlFor="newPassword" className="flex items-center">
                                    <Lock className="mr-2 h-4 w-4"/>
                                    Nowe hasło
                                </Label>
                                <Input
                                    id="newPassword"
                                    type="password"
                                    placeholder="Wprowadź nowe hasło"
                                    value={newPassword}
                                    onChange={(e) => setNewPassword(e.target.value)}
                                    required
                                />
                            </div>
                            <div className="space-y-2">
                                <Label htmlFor="confirmPassword" className="flex items-center">
                                    <Lock className="mr-2 h-4 w-4"/>
                                    Potwierdź hasło
                                </Label>
                                <Input
                                    id="confirmPassword"
                                    type="password"
                                    placeholder="Potwierdź nowe hasło"
                                    value={confirmPassword}
                                    onChange={(e) => setConfirmPassword(e.target.value)}
                                    required
                                />
                            </div>
                            <div className="space-y-2">
                                <Label htmlFor="resetToken" className="flex items-center">
                                    <Key className="mr-2 h-4 w-4"/>
                                    Token resetujący
                                </Label>
                                <Input
                                    id="resetToken"
                                    type="password"
                                    placeholder="Wprowadź token resetujący"
                                    value={resetToken}
                                    onChange={(e) => setResetToken(e.target.value)}
                                    required
                                />
                            </div>
                            <Button type="submit" className="w-full">
                                <Key className="mr-2 h-4 w-4"/>
                                Zresetuj hasło
                            </Button>
                        </form>
                    </CardContent>
                </Card>
            </div>
        </div>
    )
}