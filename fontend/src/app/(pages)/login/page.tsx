'use client';

import React, { useState } from 'react';
import * as yup from 'yup';
import { Card, CardContent, CardDescription, CardFooter, CardHeader, CardTitle } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Home, LogIn } from 'lucide-react';
import Link from 'next/link';
import { ModeToggle } from "@/components/ModeToggle";
import {jwtDecode} from 'jwt-decode';

const loginSchema = yup.object().shape({
    email: yup.string().email('Invalid email format').required('Email is required'),
    password: yup.string().min(8, 'Password must be at least 8 characters long').required('Password is required'),
});

const validateLoginData = async (data: { email: string; password: string }) => {
    try {
        await loginSchema.validate(data, { abortEarly: false });
        console.log('Validation successful');
    } catch (err) {
        if (err instanceof yup.ValidationError) {
            console.error('Validation errors:', err.errors);
        }
    }
};

function LoginForm() {
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');

    const handleSignIn = async (e: React.FormEvent<HTMLFormElement>) => {
        e.preventDefault();
        const data = { email, password };
        await validateLoginData(data);

        try {
            const response = await fetch('http://localhost:8444/bwp/hhn/api/v1/auth/login', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': 'Basic ' + btoa(email + ':' + password),
                },
            });

            if (response.ok) {
                const data = await response.json();
                const accessToken = data.accessToken;
                localStorage.setItem('jwt_accessToken', accessToken);
                const outAccessToken = localStorage.getItem('jwt_accessToken');
                if (outAccessToken) {
                    const decodedToken = jwtDecode(outAccessToken);
                    console.log('Decoded Token:', decodedToken);
                }
                console.log('Login successful:');
            } else {
                console.error('Login failed:', response.statusText);
            }
        } catch (error) {
            console.error('Error:', error);
        }
    };

    return (
        <form onSubmit={handleSignIn} className="space-y-4">
            <div className="space-y-2">
                <Label htmlFor="email">Email</Label>
                <Input
                    id="email"
                    type="email"
                    placeholder="your.email@example.com"
                    value={email}
                    onChange={(e) => setEmail(e.target.value)}
                    required
                />
            </div>
            <div className="space-y-2">
                <Label htmlFor="password">Password</Label>
                <Input
                    id="password"
                    type="password"
                    value={password}
                    onChange={(e) => setPassword(e.target.value)}
                    required
                />
            </div>
            <Button type="submit" className="w-full">
                Sign In
                <LogIn className="ml-2 h-4 w-4" />
            </Button>
        </form>
    );
}

export default function LoginPageComponent() {
    return (
        <div className="min-h-screen flex flex-col items-center justify-center bg-background text-foreground p-4">
            <div className="w-full max-w-md">
                <div className="absolute top-4 right-4">
                    <ModeToggle />
                </div>
                <Card className="w-full">
                    <CardHeader className="text-center">
                        <div className="flex justify-center mb-4">
                            <Home className="h-12 w-12 text-primary" />
                        </div>
                        <CardTitle className="text-3xl font-bold">Welcome to eBOK</CardTitle>
                        <CardDescription>
                            Your Residential Property Management System
                        </CardDescription>
                    </CardHeader>
                    <CardContent>
                        <LoginForm />
                    </CardContent>
                    <CardFooter className="flex flex-col sm:flex-row justify-between items-center space-y-2 sm:space-y-0">
                        <Link href="/forgot-password" className="text-sm text-primary hover:underline">
                            Forgot password?
                        </Link>
                        <Link href="/register" className="text-sm text-primary hover:underline">
                            New user? Register here
                        </Link>
                    </CardFooter>
                </Card>
            </div>
        </div>
    );
}