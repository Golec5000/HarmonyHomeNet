'use client';

import React, {useState} from 'react';
import * as yup from 'yup';
import {Card, CardContent, CardDescription, CardFooter, CardHeader, CardTitle} from "@/components/ui/card";
import {Button} from "@/components/ui/button";
import {Input} from "@/components/ui/input";
import {Label} from "@/components/ui/label";
import {Home, LogIn} from 'lucide-react';
import Link from 'next/link';
import {ModeToggle} from "@/components/ModeToggle";
import {jwtDecode} from 'jwt-decode';
import {toast} from "sonner";

const loginSchema = yup.object().shape({
    email: yup.string().email('Invalid email format').required('Email is required'),
    password: yup.string().min(8, 'Password must be at least 8 characters long').required('Password is required'),
});

const validateLoginData = async (data: { email: string; password: string }) => {
    try {
        await loginSchema.validate(data, {abortEarly: false});
        console.log('Validation successful');
    } catch (err) {
        if (err instanceof yup.ValidationError) {
            console.error('Validation errors:', err.errors);
        }
    }
};

interface CustomJwtPayload {
    role: string;
}

function LoginForm() {
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');

    const handleSignIn = async (e: React.FormEvent<HTMLFormElement>) => {
        e.preventDefault();
        const data = {email, password};
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
                const myAccessToken = data.accessToken;
                localStorage.setItem('jwt_accessToken', myAccessToken);

                // Decode the token to get the user's role
                const decodedToken = jwtDecode<CustomJwtPayload>(myAccessToken);
                const userRole = decodedToken.role;

                // Redirect based on the user's role
                if (userRole === 'ROLE_ADMIN' || userRole === 'ROLE_EMPLOYEE') {
                    window.location.href = '/admin-home';
                } else if (userRole === 'ROLE_OWNER') {
                    window.location.href = '/owner-home';
                } else {
                    window.location.href = '/welcome-home';
                }

                console.log('Login successful');
            } else {
                toast.error('Login failed. Please check your credentials.');
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
                <LogIn className="ml-2 h-4 w-4"/>
            </Button>
        </form>
    );
}

export default function LoginPageComponent() {
    return (
        <div className="min-h-screen flex flex-col items-center justify-center bg-background text-foreground p-4">
            <div className="w-full max-w-md">
                <div className="absolute top-4 right-4">
                    <ModeToggle/>
                </div>
                <Card className="w-full">
                    <CardHeader className="text-center">
                        <div className="flex justify-center mb-4">
                            <Home className="h-12 w-12 text-primary"/>
                        </div>
                        <CardTitle className="text-3xl font-bold">Welcome to eBOK</CardTitle>
                        <CardDescription>
                            Your Residential Property Management System
                        </CardDescription>
                    </CardHeader>
                    <CardContent>
                        <LoginForm/>
                    </CardContent>
                    <CardFooter
                        className="flex flex-col sm:flex-row justify-center items-center space-y-2 sm:space-y-0">
                        <Link href="/forgot-password" className="text-sm text-primary hover:underline">
                            Forgot password?
                        </Link>
                    </CardFooter>
                </Card>
            </div>
        </div>
    );
}