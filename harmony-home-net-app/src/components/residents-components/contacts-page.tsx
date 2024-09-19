'use client'

import React, {useEffect, useState} from 'react'
import {Card, CardContent, CardHeader, CardTitle} from "@/components/ui/card"
import {Button} from "@/components/ui/button"
import {Input} from "@/components/ui/input"
import {Label} from "@/components/ui/label"
import {Textarea} from "@/components/ui/textarea"
import {Select, SelectContent, SelectItem, SelectTrigger, SelectValue} from "@/components/ui/select"
import {MessageSquare, Phone, Mail, Clock} from 'lucide-react'

type Category = {
    id: string
    value: string
    label: string
}

export function ContactAdmin() {
    const [subject, setSubject] = useState('')
    const [message, setMessage] = useState('')
    const [category, setCategory] = useState('')
    const [categories, setCategories] = useState<Category[]>([])

    useEffect(() => {
        // Fetch categories from the backend API
        fetch('/api/categories')
            .then(response => response.json())
            .then(data => setCategories(data))
            .catch(error => console.error('Error fetching categories:', error))
    }, [])

    const handleSubmit = (e: React.FormEvent) => {
        e.preventDefault()
        console.log({subject, message, category})
        setSubject('')
        setMessage('')
        setCategory('')
        alert('Message sent to administrators!')
    }

    return (
        <div className="space-y-6 bg-gray-100 dark:bg-gray-900 text-gray-800 dark:text-gray-200">
            <h1 className="text-3xl font-bold text-gray-800 dark:text-gray-100 flex items-center">
                <MessageSquare className="mr-2 h-8 w-8 text-blue-600 dark:text-blue-400"/>
                Contact Administrators
            </h1>

            <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                <Card className="bg-white dark:bg-gray-800 text-gray-900 dark:text-gray-100">
                    <CardHeader>
                        <CardTitle>Send a Message</CardTitle>
                    </CardHeader>
                    <CardContent>
                        <form onSubmit={handleSubmit} className="space-y-4">
                            <div className="space-y-2">
                                <Label htmlFor="category">Category</Label>
                                <Select value={category} onValueChange={setCategory}>
                                    <SelectTrigger id="category">
                                        <SelectValue placeholder="Select a category"/>
                                    </SelectTrigger>
                                    <SelectContent>
                                        {categories.map(cat => (
                                            <SelectItem key={cat.id} value={cat.value}>
                                                {cat.label}
                                            </SelectItem>
                                        ))}
                                    </SelectContent>
                                </Select>
                            </div>
                            <div className="space-y-2">
                                <Label htmlFor="subject">Subject</Label>
                                <Input
                                    id="subject"
                                    value={subject}
                                    onChange={(e) => setSubject(e.target.value)}
                                    placeholder="Brief description of your issue"
                                    required
                                />
                            </div>
                            <div className="space-y-2">
                                <Label htmlFor="message">Message</Label>
                                <Textarea
                                    id="message"
                                    value={message}
                                    onChange={(e) => setMessage(e.target.value)}
                                    placeholder="Provide more details about your issue or inquiry"
                                    required
                                    className="min-h-[150px]"
                                />
                            </div>
                            <Button type="submit" className="w-full">Send Message</Button>
                        </form>
                    </CardContent>
                </Card>

                <Card className="bg-white dark:bg-gray-800 text-gray-900 dark:text-gray-100">
                    <CardHeader>
                        <CardTitle>Contact Information</CardTitle>
                    </CardHeader>
                    <CardContent className="space-y-4">
                        <div className="flex items-center space-x-2">
                            <Phone className="h-5 w-5 text-blue-600 dark:text-blue-400"/>
                            <span>Emergency: (555) 123-4567</span>
                        </div>
                        <div className="flex items-center space-x-2">
                            <Mail className="h-5 w-5 text-blue-600 dark:text-blue-400"/>
                            <span>Email: admin@ebok.com</span>
                        </div>
                        <div className="flex items-center space-x-2">
                            <Clock className="h-5 w-5 text-blue-600 dark:text-blue-400"/>
                            <span>Office Hours: Mon-Fri, 9AM-5PM</span>
                        </div>
                        <Card className="bg-gray-100 dark:bg-gray-700">
                            <CardHeader>
                                <CardTitle className="text-sm">Note</CardTitle>
                            </CardHeader>
                            <CardContent>
                                <p className="text-sm text-gray-600 dark:text-gray-300">
                                    For emergencies outside of office hours, please use the emergency phone number.
                                    For non-urgent matters, we will respond to your message within 1-2 business days.
                                </p>
                            </CardContent>
                        </Card>
                    </CardContent>
                </Card>
            </div>
        </div>
    )
}