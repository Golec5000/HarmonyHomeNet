'use client'

import React, {useEffect, useState} from 'react'
import {z} from 'zod'
import {useForm} from 'react-hook-form'
import {zodResolver} from '@hookform/resolvers/zod'
import {format} from 'date-fns'
import {toast} from 'sonner'
import {Table, TableBody, TableCell, TableHead, TableHeader, TableRow,} from "@/components/ui/table"
import {
    Dialog,
    DialogContent,
    DialogDescription,
    DialogFooter,
    DialogHeader,
    DialogTitle,
    DialogTrigger,
} from "@/components/ui/dialog"
import {
    DropdownMenu,
    DropdownMenuContent,
    DropdownMenuItem,
    DropdownMenuLabel,
    DropdownMenuSeparator,
    DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu"
import {Button} from "@/components/ui/button"
import {Input} from "@/components/ui/input"
import {Label} from "@/components/ui/label"
import {Textarea} from "@/components/ui/textarea"
import {Checkbox} from "@/components/ui/checkbox"
import {Card, CardContent, CardHeader, CardTitle,} from "@/components/ui/card"
import {ChevronDown, ChevronLeft, ChevronRight, ChevronUp, Megaphone, MoreHorizontal, Plus, Trash2} from 'lucide-react'
import {Collapsible, CollapsibleContent, CollapsibleTrigger} from "@/components/ui/collapsible"
import {jwtDecode} from "jwt-decode"

interface jwtCustomClaims {
    userId: string
}

interface Announcement {
    id: number
    title: string
    content: string
    createdAt: string
    updatedAt: string
}

interface Apartment {
    apartmentId: string
    address: string
    city: string
    zipCode: string
    apartmentArea: number
    createdAt: string
    updatedAt: string
    apartmentSignature: string
    apartmentPercentValue: number
}

interface PageResponse<T> {
    currentPage: number
    pageSize: number
    totalPages: number
    content: T[]
    last: boolean
    hasNext: boolean
    hasPrevious: boolean
}

const announcementSchema = z.object({
    title: z.string().min(1).max(50),
    content: z.string().min(1).max(1000),
    userId: z.string().uuid(),
})

export function AnnouncementManagement() {
    const [announcements, setAnnouncements] = useState<Announcement[]>([])
    const [apartments, setApartments] = useState<Apartment[]>([])
    const [currentPage, setCurrentPage] = useState(0)
    const [totalPages, setTotalPages] = useState(0)
    const [isAddAnnouncementDialogOpen, setIsAddAnnouncementDialogOpen] = useState(false)
    const [isEditAnnouncementDialogOpen, setIsEditAnnouncementDialogOpen] = useState(false)
    const [editingAnnouncement, setEditingAnnouncement] = useState<Announcement | null>(null)
    const [selectedApartments, setSelectedApartments] = useState<string[]>([])
    const [apartmentPage, setApartmentPage] = useState(0)
    const [totalApartmentPages, setTotalApartmentPages] = useState(0)
    const [isLinkApartmentsDialogOpen, setIsLinkApartmentsDialogOpen] = useState(false)
    const [expandedAnnouncementId] = useState<number | null>(null)
    const [selectedAnnouncementId, setSelectedAnnouncementId] = useState<number | null>(null)

    const {register, handleSubmit, reset, formState: {errors}} = useForm<z.infer<typeof announcementSchema>>({
        resolver: zodResolver(announcementSchema),
    })

    useEffect(() => {
        fetchAnnouncements()
    }, [currentPage])

    useEffect(() => {
        fetchApartments()
    }, [apartmentPage])

    const fetchAnnouncements = async () => {
        try {
            const response = await fetch(`http://localhost:8444/bwp/hhn/api/v1/announcement/get-all-announcements?pageNo=${currentPage}&pageSize=5`, {
                headers: {
                    'Authorization': `Bearer ${sessionStorage.getItem('jwt_accessToken')}`
                }
            })
            if (response.ok) {
                const data: PageResponse<Announcement> = await response.json()
                setAnnouncements(data.content)
                setTotalPages(data.totalPages)
            } else {
                toast.error('Failed to fetch announcements')
            }
        } catch (error) {
            console.error('Error fetching announcements:', error)
            toast.error('An error occurred while fetching announcements')
        }
    }

    const fetchAnnouncementDetails = async (announcementId: number) => {
        try {
            const response = await fetch(`http://localhost:8444/bwp/hhn/api/v1/announcement/get-announcement/${announcementId}`, {
                headers: {
                    'Authorization': `Bearer ${sessionStorage.getItem('jwt_accessToken')}`
                }
            });
            if (response.ok) {
                const data = await response.json();
                setEditingAnnouncement({
                    id: announcementId,
                    title: data.title,
                    content: data.content,
                    createdAt: data.createdAt,
                    updatedAt: data.updatedAt
                });
            } else {
                toast.error('Failed to fetch announcement details');
            }
        } catch (error) {
            console.error('Error fetching announcement details:', error);
            toast.error('An error occurred while fetching announcement details');
        }
    };

    const handleEditAnnouncement = (announcementId: number) => {
        fetchAnnouncementDetails(announcementId);
        setIsEditAnnouncementDialogOpen(true);
    };

    const fetchApartments = async () => {
        try {
            const response = await fetch(`http://localhost:8444/bwp/hhn/api/v1/apartment/get-all-apartments?pageNo=${apartmentPage}&pageSize=5`, {
                headers: {
                    'Authorization': `Bearer ${sessionStorage.getItem('jwt_accessToken')}`
                }
            })
            if (response.ok) {
                const data: PageResponse<Apartment> = await response.json()
                setApartments(data.content)
                setTotalApartmentPages(data.totalPages)
            } else {
                toast.error('Failed to fetch apartments')
            }
        } catch (error) {
            console.error('Error fetching apartments:', error)
            toast.error('An error occurred while fetching apartments')
        }
    }

    const handleCreateAnnouncement = async (data: z.infer<typeof announcementSchema>) => {
        try {
            const token = sessionStorage.getItem('jwt_accessToken');
            if (!token) {
                toast.error('User not authenticated');
                return;
            }

            const decodedToken = jwtDecode<jwtCustomClaims>(token);
            const userId = decodedToken.userId;

            const response = await fetch('http://localhost:8444/bwp/hhn/api/v1/announcement/create-announcement', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${token}`
                },
                body: JSON.stringify({...data, userId})
            });

            if (response.ok) {
                toast.success('Announcement created successfully');
                setIsAddAnnouncementDialogOpen(false);
                reset();
                fetchAnnouncements();
            } else {
                toast.error('Failed to create announcement');
            }
        } catch (error) {
            console.error('Error creating announcement:', error);
            toast.error('An error occurred while creating the announcement');
        }
    };

    const handleUpdateAnnouncement = async (data: z.infer<typeof announcementSchema>) => {
        if (editingAnnouncement) {
            try {
                const token = sessionStorage.getItem('jwt_accessToken');
                if (!token) {
                    toast.error('User not authenticated');
                    return;
                }

                const response = await fetch(`http://localhost:8444/bwp/hhn/api/v1/announcement/update-announcement/${editingAnnouncement.id}`, {
                    method: 'PUT',
                    headers: {
                        'Content-Type': 'application/json',
                        'Authorization': `Bearer ${token}`
                    },
                    body: JSON.stringify(data)
                });

                if (response.ok) {
                    toast.success('Announcement updated successfully');
                    setIsEditAnnouncementDialogOpen(false);
                    setEditingAnnouncement(null);
                    reset();
                    fetchAnnouncements();
                } else {
                    toast.error('Failed to update announcement');
                }
            } catch (error) {
                console.error('Error updating announcement:', error);
                toast.error('An error occurred while updating the announcement');
            }
        }
    };

    const handleDeleteAnnouncement = async (id: number) => {
        try {
            const response = await fetch(`http://localhost:8444/bwp/hhn/api/v1/announcement/delete-announcement/${id}`, {
                method: 'DELETE',
                headers: {
                    'Authorization': `Bearer ${sessionStorage.getItem('jwt_accessToken')}`
                }
            })
            if (response.ok) {
                toast.success('Announcement deleted successfully')
                fetchAnnouncements()
            } else {
                toast.error('Failed to delete announcement')
            }
        } catch (error) {
            console.error('Error deleting announcement:', error)
            toast.error('An error occurred while deleting the announcement')
        }
    }

    const handleLinkAnnouncement = async () => {
        console.log(selectedApartments)
        if (selectedAnnouncementId === null) {
            toast.error('No announcement selected');
            return;
        }
        try {

            const response = await fetch(`http://localhost:8444/bwp/hhn/api/v1/announcement/link-announcement-to-apartments/${selectedAnnouncementId}`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${sessionStorage.getItem('jwt_accessToken')}`
                },
                body: JSON.stringify(selectedApartments)
            })
            if (response.ok) {
                toast.success('Announcement linked to apartments successfully')
                setSelectedApartments([])
                setIsLinkApartmentsDialogOpen(false)
            } else {
                toast.error('Failed to link announcement to apartments')
            }
        } catch (error) {
            console.error('Error linking announcement to apartments:', error)
            toast.error('An error occurred while linking the announcement to apartments')
        }
    }

    const handleUnlinkAnnouncement = async () => {
        if (selectedAnnouncementId === null) {
            toast.error('No announcement selected');
            return;
        }
        try {
            const response = await fetch(`http://localhost:8444/bwp/hhn/api/v1/announcement/unlink-announcement-from-apartments/${selectedAnnouncementId}`, {
                method: 'DELETE',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${sessionStorage.getItem('jwt_accessToken')}`
                },
                body: JSON.stringify(selectedApartments)
            })
            if (response.ok) {
                toast.success('Announcement unlinked from apartments successfully')
                setSelectedApartments([])
                setIsLinkApartmentsDialogOpen(false)
            } else {
                toast.error('Failed to unlink announcement from apartments')
            }
        } catch (error) {
            console.error('Error unlinking announcement from apartments:', error)
            toast.error('An error occurred while unlinking the announcement from apartments')
        }
    }

    return (
        <Card>
            <CardHeader>
                <CardTitle className="text-2xl font-bold flex items-center space-x-2">
                    <Megaphone className="h-6 w-6 text-primary"/>
                    <span>Zarządzanie Ogłoszeniami</span>
                </CardTitle>
            </CardHeader>
            <CardContent>
                <div className="flex justify-end items-center mb-4">
                    <Dialog open={isAddAnnouncementDialogOpen} onOpenChange={setIsAddAnnouncementDialogOpen}>
                        <DialogTrigger asChild>
                            <Button>
                                <Plus className="mr-2 h-4 w-4"/> Dodaj Ogłoszenie
                            </Button>
                        </DialogTrigger>
                        <DialogContent className="sm:max-w-[425px]">
                            <DialogHeader>
                                <DialogTitle>Dodaj Nowe Ogłoszenie</DialogTitle>
                                <DialogDescription>
                                    Wprowadź dane nowego ogłoszenia. Kliknij zapisz, gdy skończysz.
                                </DialogDescription>
                            </DialogHeader>
                            <form onSubmit={handleSubmit(handleCreateAnnouncement)}>
                                <div className="grid gap-4 py-4">
                                    <div className="grid grid-cols-4 items-center gap-4">
                                        <Label htmlFor="title" className="text-right">
                                            Tytuł
                                        </Label>
                                        <Input
                                            id="title"
                                            className="col-span-3"
                                            {...register('title')}
                                        />
                                    </div>
                                    {errors.title && <p className="text-red-500 text-sm">{errors.title.message}</p>}
                                    <div className="grid grid-cols-4 items-center gap-4">
                                        <Label htmlFor="content" className="text-right">
                                            Treść
                                        </Label>
                                        <Textarea
                                            id="content"
                                            className="col-span-3"
                                            {...register('content')}
                                        />
                                    </div>
                                    {errors.content && <p className="text-red-500 text-sm">{errors.content.message}</p>}
                                    <input type="hidden" {...register('userId')}
                                           value="00000000-0000-0000-0000-000000000000"/>
                                </div>
                                <DialogFooter>
                                    <Button type="submit">Zapisz</Button>
                                </DialogFooter>
                            </form>
                        </DialogContent>
                    </Dialog>
                </div>
                <Table>
                    <TableHeader>
                        <TableRow>
                            <TableHead>ID</TableHead>
                            <TableHead>Tytuł</TableHead>
                            <TableHead>Treść</TableHead>
                            <TableHead>Data utworzenia</TableHead>
                            <TableHead>Data aktualizacji</TableHead>
                            <TableHead>Akcje</TableHead>
                        </TableRow>
                    </TableHeader>
                    <TableBody>
                        {announcements.map((announcement) => (
                            <React.Fragment key={announcement.id}>
                                <TableRow>
                                    <TableCell>{announcement.id}</TableCell>
                                    <TableCell>{announcement.title}</TableCell>
                                    <TableCell>
                                        <Collapsible>
                                            <CollapsibleTrigger asChild>
                                                <Button variant="ghost" size="sm" className="p-0">
                                                    {expandedAnnouncementId === announcement.id ? (
                                                        <ChevronUp className="h-4 w-4 mr-2"/>
                                                    ) : (
                                                        <ChevronDown className="h-4 w-4 mr-2"/>
                                                    )}
                                                    {announcement.content.substring(0, 50)}...
                                                </Button>
                                            </CollapsibleTrigger>
                                            <CollapsibleContent className="mt-2">
                                                {announcement.content}
                                            </CollapsibleContent>
                                        </Collapsible>
                                    </TableCell>
                                    <TableCell>{format(new Date(announcement.createdAt), 'dd-MM-yyyy HH:mm:ss')}</TableCell>
                                    <TableCell>{format(new Date(announcement.updatedAt), 'dd-MM-yyyy HH:mm:ss')}</TableCell>
                                    <TableCell>
                                        <DropdownMenu>
                                            <DropdownMenuTrigger asChild>
                                                <Button variant="ghost" className="h-8 w-8 p-0">
                                                    <span className="sr-only">Open menu</span>
                                                    <MoreHorizontal className="h-4 w-4"/>
                                                </Button>
                                            </DropdownMenuTrigger>
                                            <DropdownMenuContent align="end">
                                                <DropdownMenuLabel>Akcje</DropdownMenuLabel>
                                                <DropdownMenuItem
                                                    onClick={() => handleEditAnnouncement(announcement.id)}>
                                                    Edytuj Ogłoszenie
                                                </DropdownMenuItem>
                                                <DropdownMenuItem onClick={() => {
                                                    setIsLinkApartmentsDialogOpen(true);
                                                    setSelectedApartments([]);
                                                    setSelectedAnnouncementId(announcement.id);
                                                }}>
                                                    Zarządznie ogłoszniemi do mieszkań
                                                </DropdownMenuItem>
                                                <DropdownMenuSeparator/>
                                                <DropdownMenuItem
                                                    onClick={() => handleDeleteAnnouncement(announcement.id)}
                                                    className="text-red-600"
                                                >
                                                    <Trash2 className="mr-2 h-4 w-4"/>
                                                    Usuń Ogłoszenie
                                                </DropdownMenuItem>
                                            </DropdownMenuContent>
                                        </DropdownMenu>
                                    </TableCell>
                                </TableRow>
                            </React.Fragment>
                        ))}
                    </TableBody>
                </Table>
                <div className="flex justify-between items-center mt-4">
                    <Button
                        variant="outline"
                        size="sm"
                        onClick={() => setCurrentPage(prev => Math.max(prev - 1, 0))}
                        disabled={currentPage === 0}
                    >
                        <ChevronLeft className="h-4 w-4 mr-2"/>
                        Poprzednia
                    </Button>
                    <div className="text-sm font-medium">
                        Strona {currentPage + 1} z {totalPages}
                    </div>
                    <Button
                        variant="outline"
                        size="sm"
                        onClick={() => setCurrentPage(prev => Math.min(prev + 1, totalPages - 1))}
                        disabled={currentPage === totalPages - 1}
                    >
                        Następna
                        <ChevronRight className="h-4 w-4 ml-2"/>
                    </Button>
                </div>
            </CardContent>
            <Dialog open={isEditAnnouncementDialogOpen} onOpenChange={setIsEditAnnouncementDialogOpen}>
                <DialogContent className="sm:max-w-[425px]">
                    <DialogHeader>
                        <DialogTitle>Edytuj Ogłoszenie</DialogTitle>
                        <DialogDescription>
                            Wprowadź nowe dane ogłoszenia. Kliknij zapisz, gdy skończysz.
                        </DialogDescription>
                    </DialogHeader>
                    {editingAnnouncement && (
                        <form onSubmit={handleSubmit(handleUpdateAnnouncement)}>
                            <div className="grid gap-4 py-4">
                                <div className="grid grid-cols-4 items-center gap-4">
                                    <Label htmlFor="edit-title" className="text-right">
                                        Tytuł
                                    </Label>
                                    <Input
                                        id="edit-title"
                                        defaultValue={editingAnnouncement.title}
                                        className="col-span-3"
                                        {...register('title')}
                                    />
                                </div>
                                {errors.title && <p className="text-red-500 text-sm">{errors.title.message}</p>}
                                <div className="grid grid-cols-4 items-center gap-4">
                                    <Label htmlFor="edit-content" className="text-right">
                                        Treść
                                    </Label>
                                    <Textarea
                                        id="edit-content"
                                        defaultValue={editingAnnouncement.content}
                                        className="col-span-3"
                                        {...register('content')}
                                    />
                                </div>
                                {errors.content && <p className="text-red-500 text-sm">{errors.content.message}</p>}
                                <input type="hidden" {...register('userId')}
                                       value="00000000-0000-0000-0000-000000000000"/>
                            </div>
                            <DialogFooter>
                                <Button type="submit">Zapisz</Button>
                            </DialogFooter>
                        </form>
                    )}
                </DialogContent>
            </Dialog>
            <Dialog open={isLinkApartmentsDialogOpen} onOpenChange={setIsLinkApartmentsDialogOpen}>
                <DialogContent className="sm:max-w-[425px]">
                    <DialogHeader>
                        <DialogTitle>Wybierz Mieszkania</DialogTitle>
                        <DialogDescription>
                            Zaznacz mieszkania, do których chcesz przypisać lub od których chcesz odłączyć ogłoszenie.
                        </DialogDescription>
                    </DialogHeader>
                    <div className="grid gap-4 py-4">
                        {apartments.map((apartment) => (
                            <div key={apartment.apartmentId} className="flex items-center space-x-2">
                                <Checkbox
                                    id={apartment.apartmentId}
                                    checked={selectedApartments.includes(apartment.apartmentSignature)}
                                    onCheckedChange={(checked) => {
                                        setSelectedApartments(prev =>
                                            checked
                                                ? [...prev, apartment.apartmentSignature]
                                                : prev.filter(sig => sig !== apartment.apartmentSignature)
                                        );
                                    }}
                                />
                                <Label htmlFor={apartment.apartmentId}>{apartment.address}</Label>
                            </div>
                        ))}
                    </div>
                    <div className="flex items-center justify-between">
                        <Button
                            variant="outline"
                            size="sm"
                            onClick={() => setApartmentPage(prev => Math.max(prev - 1, 0))}
                            disabled={apartmentPage === 0}
                        >
                            <ChevronLeft className="h-4 w-4 mr-2"/>
                            Poprzednia
                        </Button>
                        <div className="text-sm font-medium">
                            Strona {apartmentPage + 1} z {totalApartmentPages}
                        </div>
                        <Button
                            variant="outline"
                            size="sm"
                            onClick={() => setApartmentPage(prev => Math.min(prev + 1, totalApartmentPages - 1))}
                            disabled={apartmentPage === totalApartmentPages - 1}
                        >
                            Następna
                            <ChevronRight className="h-4 w-4 ml-2"/>
                        </Button>
                    </div>
                    <DialogFooter>
                        <Button onClick={handleLinkAnnouncement}>Przypisz</Button>
                        <Button onClick={handleUnlinkAnnouncement} variant="outline">Odłącz</Button>
                    </DialogFooter>
                </DialogContent>
            </Dialog>
        </Card>
    )
}