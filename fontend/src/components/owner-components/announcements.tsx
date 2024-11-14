'use client'

import React, {useState, useEffect} from 'react';
import {Card, CardContent, CardHeader, CardTitle} from '@/components/ui/card';
import {Button} from '@/components/ui/button';
import {Bell, ChevronLeft, ChevronRight, FileText} from 'lucide-react';
import {format, parseISO} from 'date-fns';

interface Announcement {
    id: number;
    title: string;
    content: string;
    createdAt: string;
    updatedAt: string;
}

interface PageResponse {
    content: Announcement[];
    totalPages: number;
    totalElements: number;
    size: number;
    number: number;
    hasNext: boolean;
    hasPrev: boolean;
}

interface AnnouncementsProps {
    apartmentSignature: string | null;
}

export default function Announcements({apartmentSignature}: AnnouncementsProps) {
    const [announcements, setAnnouncements] = useState<Announcement[]>([]);
    const [currentPage, setCurrentPage] = useState(0);
    const [totalPages, setTotalPages] = useState(0);

    const fetchAnnouncements = async (page: number) => {
        if (!apartmentSignature) {
            console.log('No apartment signature provided');
            return;
        }

        try {
            const response = await fetch(`http://localhost:8444/bwp/hhn/api/v1/announcement/get-announcements-by-apartment?apartmentSignature=${apartmentSignature}&pageNo=${page}&pageSize=5`, {
                headers: {
                    'Authorization': `Bearer ${localStorage.getItem('jwt_accessToken')}`
                },
            });

            if (response.ok) {
                const data: PageResponse = await response.json();
                console.log('Fetched data:', data);
                setAnnouncements(data.content);
                setTotalPages(data.totalPages);
            } else if (response.status === 401 || response.status === 403) {
                window.location.href = '/login';
            } else {
                console.error('Failed to fetch announcements:', response.statusText);
            }
        } catch (error) {
            console.error('Error fetching announcements:', error);
        }
    };

    useEffect(() => {
        console.log('Fetching announcements on component mount or update');
        fetchAnnouncements(currentPage);
    }, [currentPage, apartmentSignature]);

    const handlePrevious = () => {
        if (currentPage > 0) {
            console.log('Going to previous page');
            setCurrentPage((prev) => prev - 1);
        }
    };

    const handleNext = () => {
        if (currentPage < totalPages - 1) {
            console.log('Going to next page');
            setCurrentPage((prev) => prev + 1);
        }
    };

    return (
        <div className="space-y-6">
            <h1 className="text-3xl font-bold flex items-center">
                <Bell className="mr-2 h-8 w-8 text-primary"/>
                Documents
            </h1>
            <div className="flex justify-between items-center">
                <div className="flex items-center space-x-2">
                    <Button
                        variant="outline"
                        size="sm"
                        onClick={handlePrevious}
                        disabled={currentPage === 0}
                    >
                        <ChevronLeft className="h-4 w-4"/>
                        Poprzednia
                    </Button>
                    <span className="text-sm">
                        Page {currentPage + 1} of {totalPages}
                    </span>
                    <Button
                        variant="outline"
                        size="sm"
                        onClick={handleNext}
                        disabled={currentPage === totalPages - 1}
                    >
                        Następna
                        <ChevronRight className="h-4 w-4"/>
                    </Button>
                </div>
            </div>

            <div className="space-y-4">
                {announcements.map((announcement) => (
                    <Card key={announcement.id}>
                        <CardHeader className="flex flex-row items-center space-x-2">
                            <Bell className="h-6 w-6 text-primary"/>
                            <CardTitle>{announcement.title}</CardTitle>
                        </CardHeader>
                        <CardContent>
                            <p className="text-sm text-muted-foreground mb-2">
                                Stworzone: {announcement.createdAt ? format(parseISO(announcement.createdAt), 'MMMM d, yyyy HH:mm:ss') : 'Invalid date'}
                            </p>
                            <p className="text-sm text-muted-foreground mb-2">
                                Zaktualizowane: {announcement.updatedAt ? format(parseISO(announcement.updatedAt), 'MMMM d, yyyy HH:mm:ss') : 'Invalid date'}
                            </p>
                            <p>{announcement.content}</p>
                        </CardContent>
                    </Card>
                ))}
            </div>

            {announcements.length === 0 && (
                <Card>
                    <CardContent className="text-center py-6">
                        <p className="text-muted-foreground">No announcements found.</p>
                    </CardContent>
                </Card>
            )}
        </div>
    );
}