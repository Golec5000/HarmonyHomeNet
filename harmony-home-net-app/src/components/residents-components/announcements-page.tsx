'use client'

import React, {useState} from 'react'
import {Card, CardContent, CardHeader, CardTitle} from "@/components/ui/card"
import {Button} from "@/components/ui/button"
import {Table, TableBody, TableCell, TableHead, TableHeader, TableRow} from "@/components/ui/table"
import {AlertCircle, Calendar, ChevronDown, ChevronUp, Bell} from 'lucide-react'

// Simulated database of announcements
const announcementsData = [
    {
        id: 1,
        title: 'Community Picnic',
        date: '2023-07-15',
        priority: 'Low',
        details: 'Join us for our annual community picnic at the central park. Bring your favorite dish for a potluck-style event. Games and activities will be provided for all ages.',
        organizer: 'Community Events Committee',
        location: 'Central Park'
    },
    {
        id: 2,
        title: 'Maintenance: Water Shut-off',
        date: '2023-07-18',
        priority: 'High',
        details: 'There will be a scheduled water shut-off for routine maintenance. Please store enough water for your needs during this period.',
        affectedAreas: 'All buildings',
        duration: '8:00 AM to 2:00 PM'
    },
    {
        id: 3,
        title: 'New Recycling Guidelines',
        date: '2023-07-20',
        priority: 'Medium',
        details: 'We are implementing new recycling guidelines to improve our environmental impact. Please review the attached document for detailed information on proper waste sorting.',
        effectiveDate: '2023-08-01',
        contactPerson: 'John Doe, Environmental Coordinator'
    },
    {
        id: 4,
        title: 'Resident Survey',
        date: '2023-07-25',
        priority: 'Low',
        details: 'We value your feedback! Please participate in our annual resident satisfaction survey. Your responses will help us improve our services.',
        surveyLink: 'https://example.com/survey',
        deadline: '2023-08-10'
    },
    {
        id: 5,
        title: 'Building Security Update',
        date: '2023-07-30',
        priority: 'High',
        details: 'We are upgrading our building security system. New key fobs will be distributed to all residents. Please check your mailbox for more information.',
        implementationDate: '2023-08-15',
        securityOfficeHours: 'Mon-Fri, 9 AM - 5 PM'
    },
]

export function Announcements() {
    const [expandedAnnouncement, setExpandedAnnouncement] = useState<number | null>(null)

    const toggleExpand = (id: number) => {
        setExpandedAnnouncement(expandedAnnouncement === id ? null : id)
    }

    return (
        <div className="space-y-6 bg-gray-100 dark:bg-gray-900 text-gray-800 dark:text-gray-200">
            <div className="flex justify-between items-center">
                <h1 className="text-3xl font-bold text-gray-800 dark:text-gray-100 flex items-center">
                    <Bell className="mr-2 h-8 w-8 text-yellow-600 dark:text-yellow-400"/>
                    Announcements
                </h1>
            </div>

            <Card className="bg-white dark:bg-gray-800 text-gray-900 dark:text-gray-100">
                <CardHeader>
                    <CardTitle>Recent Announcements</CardTitle>
                </CardHeader>
                <CardContent>
                    <Table>
                        <TableHeader>
                            <TableRow>
                                <TableHead>Title</TableHead>
                                <TableHead>Date</TableHead>
                                <TableHead>Priority</TableHead>
                                <TableHead>Action</TableHead>
                            </TableRow>
                        </TableHeader>
                        <TableBody>
                            {announcementsData.map((announcement) => (
                                <React.Fragment key={announcement.id}>
                                    <TableRow>
                                        <TableCell className="font-medium">
                                            <div className="flex items-center">
                                                <AlertCircle className={`mr-2 h-4 w-4 ${
                                                    announcement.priority === 'High' ? 'text-red-600 dark:text-red-400' :
                                                        announcement.priority === 'Medium' ? 'text-yellow-600 dark:text-yellow-400' :
                                                            'text-green-600 dark:text-green-400'
                                                }`}/>
                                                {announcement.title}
                                            </div>
                                        </TableCell>
                                        <TableCell>
                                            <div className="flex items-center">
                                                <Calendar className="mr-2 h-4 w-4 text-blue-600 dark:text-blue-400"/>
                                                {announcement.date}
                                            </div>
                                        </TableCell>
                                        <TableCell>
                                        <span className={`px-2 py-1 rounded-full text-xs font-semibold ${
                                            announcement.priority === 'High' ? 'bg-red-100 text-red-800 dark:bg-red-800 dark:text-red-100' :
                                                announcement.priority === 'Medium' ? 'bg-yellow-100 text-yellow-800 dark:bg-yellow-800 dark:text-yellow-100' :
                                                    'bg-green-100 text-green-800 dark:bg-green-800 dark:text-green-100'
                                        }`}>
                                            {announcement.priority}
                                        </span>
                                        </TableCell>
                                        <TableCell>
                                            <Button
                                                variant="ghost"
                                                size="sm"
                                                onClick={() => toggleExpand(announcement.id)}
                                            >
                                                {expandedAnnouncement === announcement.id ? 'Hide Details' : 'View Details'}
                                                {expandedAnnouncement === announcement.id ?
                                                    <ChevronUp className="ml-2 h-4 w-4"/> :
                                                    <ChevronDown className="ml-2 h-4 w-4"/>
                                                }
                                            </Button>
                                        </TableCell>
                                    </TableRow>
                                    {expandedAnnouncement === announcement.id && (
                                        <TableRow>
                                            <TableCell colSpan={4}>
                                                <Card className="mt-2 bg-gray-50 dark:bg-gray-700">
                                                    <CardContent className="p-4">
                                                        <h3 className="font-bold mb-2">Announcement Details</h3>
                                                        <p className="mb-2">{announcement.details}</p>
                                                        {Object.entries(announcement).map(([key, value]) => {
                                                            if (!['id', 'title', 'date', 'priority', 'details'].includes(key)) {
                                                                return (
                                                                    <p key={key} className="text-sm">
                                                                        <span
                                                                            className="font-semibold">{key.charAt(0).toUpperCase() + key.slice(1)}:</span> {value}
                                                                    </p>
                                                                )
                                                            }
                                                            return null
                                                        })}
                                                    </CardContent>
                                                </Card>
                                            </TableCell>
                                        </TableRow>
                                    )}
                                </React.Fragment>
                            ))}
                        </TableBody>
                    </Table>
                </CardContent>
            </Card>
        </div>
    )
}