'use client'

import React, {useEffect, useState} from 'react'
import {Table, TableBody, TableCell, TableHead, TableHeader, TableRow,} from "@/components/ui/table"
import {Collapsible, CollapsibleContent, CollapsibleTrigger} from "@/components/ui/collapsible";
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
import {
    ChevronDown,
    ChevronLeft,
    ChevronRight,
    ChevronUp,
    Home,
    MoreHorizontal,
    Plus,
    Trash2,
    UserMinus,
    UserPlus,
    Users
} from 'lucide-react'
import {Card, CardContent, CardHeader, CardTitle} from "@/components/ui/card"
import {
    Dialog,
    DialogContent,
    DialogDescription,
    DialogFooter,
    DialogHeader,
    DialogTitle,
    DialogTrigger,
} from "@/components/ui/dialog"
import {Label} from "@/components/ui/label"
import {format, parseISO} from 'date-fns'
import {z} from 'zod'
import {toast} from 'sonner'

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

interface UserResponse {
    firstName: string
    lastName: string
    email: string
}

export function ApartmentManagement() {
    const [apartments, setApartments] = useState<Apartment[]>([])
    const [currentPage, setCurrentPage] = useState(0)
    const [totalPages, setTotalPages] = useState(0)
    const [editingApartment, setEditingApartment] = useState<Apartment | null>(null)
    const [isAddApartmentDialogOpen, setIsAddApartmentDialogOpen] = useState(false)
    const [isAssignUserDialogOpen, setIsAssignUserDialogOpen] = useState(false)
    const [isRemoveUserDialogOpen, setIsRemoveUserDialogOpen] = useState(false)
    const [selectedApartmentId, setSelectedApartmentId] = useState<string | null>(null)
    const [userId, setUserId] = useState('')
    const [expandedApartment, setExpandedApartment] = useState<string | null>(null)
    const [residents, setResidents] = useState<UserResponse[]>([])
    const [newApartment, setNewApartment] = useState<Omit<Apartment, 'apartmentId' | 'createdAt' | 'updatedAt'>>({
        address: '',
        city: '',
        zipCode: '',
        apartmentArea: 0,
        apartmentSignature: '',
        apartmentPercentValue: 0
    })

    const fetchApartments = async () => {
        const token = sessionStorage.getItem('jwt_accessToken')
        try {
            const response = await fetch(`http://localhost:8444/bwp/hhn/api/v1/apartment/get-all-apartments?pageNo=${currentPage}&pageSize=5`, {
                headers: {
                    'Authorization': `Bearer ${token}`
                }
            })
            if (response.ok) {
                const data: PageResponse<Apartment> = await response.json()
                setApartments(data.content)
                setTotalPages(data.totalPages)
            } else {
                console.error('Failed to fetch apartments')
            }
        } catch (error) {
            console.error('An error occurred:', error)
        }
    }

    useEffect(() => {
        fetchApartments()
    }, [currentPage])

    const handleEdit = (apartment: Apartment) => {
        setEditingApartment(apartment)
    }

    const handleSaveEdit = async () => {
        if (editingApartment) {
            try {
                const token = sessionStorage.getItem('jwt_accessToken')
                const url = `http://localhost:8444/bwp/hhn/api/v1/apartment/update-apartment?apartmentSignature=${editingApartment.apartmentSignature}`

                const apartmentRequest = {
                    address: editingApartment.address,
                    city: editingApartment.city,
                    zipCode: editingApartment.zipCode,
                    apartmentArea: editingApartment.apartmentArea,
                    apartmentSignature: editingApartment.apartmentSignature,
                    apartmentPercentValue: editingApartment.apartmentPercentValue
                }

                const response = await fetch(url, {
                    method: 'PUT',
                    headers: {
                        'Content-Type': 'application/json',
                        'Authorization': `Bearer ${token}`
                    },
                    body: JSON.stringify(apartmentRequest)
                })

                if (response.ok) {
                    toast.success('Mieszkanie zostało zaktualizowane pomyślnie')
                    setEditingApartment(null)
                    fetchApartments()
                } else if (response.status === 401) {
                    toast.error('Nieautoryzowany: Nie posiadasz uprawnień do tej opcji')
                } else {
                    const errorData = await response.json()
                    toast.error(`Błąd: ${errorData.message || 'Nie udało się zaktualizować mieszkania'}`)
                }
            } catch (error) {
                console.error('An error occurred:', error)
                toast.error('Wystąpił nieoczekiwany błąd')
            }
        }
    }

    const handleDelete = async (apartmentSig: string) => {
        try {
            const token = sessionStorage.getItem('jwt_accessToken')
            const params = new URLSearchParams({
                apartmentSignature: apartmentSig,
            })
            const url = `http://localhost:8444/bwp/hhn/api/v1/apartment/delete-apartment?${params.toString()}`

            const response = await fetch(url, {
                method: 'DELETE',
                headers: {
                    'Authorization': `Bearer ${token}`
                }
            })

            if (response.ok) {
                toast.success('Mieszkanie zostało usunięte pomyślnie')
                fetchApartments()
            } else if (response.status === 401) {
                toast.error('Nieautoryzowany: Nie posiadasz uprawnień do tej opcji')
            } else {
                const errorData = await response.json()
                toast.error(`Błąd: ${errorData.message || 'Nie udało się usunąć mieszkania'}`)
            }
        } catch (error) {
            console.error('An error occurred:', error)
            toast.error('Wystąpił nieoczekiwany błąd')
        }
    }

    const apartmentSchema = z.object({
        address: z.string().min(1).max(50),
        city: z.string().min(1).max(20),
        apartmentSignature: z.string().min(1).max(60),
        zipCode: z.string().regex(/^\d{2}-\d{3}$/, 'Invalid zip code format'),
        apartmentArea: z.number().positive().multipleOf(0.01),
        apartmentPercentValue: z.number().positive().max(100).multipleOf(0.01)
    })

    const handleAddApartment = async () => {
        try {
            const validatedData = apartmentSchema.parse(newApartment)

            const token = sessionStorage.getItem('jwt_accessToken')

            const response = await fetch(`http://localhost:8444/bwp/hhn/api/v1/apartment/create-apartment`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${token}`
                },
                body: JSON.stringify(validatedData)
            })

            if (response.ok) {
                toast.success('Mieszkanie zostało dodane pomyślnie')
                setIsAddApartmentDialogOpen(false)
                setNewApartment({
                    address: '',
                    city: '',
                    zipCode: '',
                    apartmentArea: 0,
                    apartmentSignature: '',
                    apartmentPercentValue: 0
                })
                fetchApartments()
            } else if (response.status === 401) {
                toast.error('Nieautoryzowany: Nie posiadasz uprawnień do tej opcji')
            } else {
                const errorData = await response.json()
                toast.error(`Błąd: ${errorData.message || 'Nie udało się dodać mieszkania'}`)
            }
        } catch (error) {
            if (error instanceof z.ZodError) {
                const errorMessages = error.errors.map(err => err.message).join(', ')
                toast.error(`Błąd walidacji: ${errorMessages}`)
            } else {
                console.error('An error occurred:', error)
                toast.error('Wystąpił nieoczekiwany błąd')
            }
        }
    }

    const handleAssignUser = async () => {
        if (selectedApartmentId && userId) {
            try {

                const token = sessionStorage.getItem('jwt_accessToken')
                const params = new URLSearchParams({
                    apartmentSignature: selectedApartmentId,
                    userId: userId
                })
                const url = `http://localhost:8444/bwp/hhn/api/v1/apartment/create-possession-history?${params.toString()}`

                const response = await fetch(url, {
                    method: 'POST',
                    headers: {
                        'Authorization': `Bearer ${token}`
                    }
                })

                if (response.ok) {
                    toast.success('Użytkownik został przypisany do mieszkania pomyślnie')
                    setIsAssignUserDialogOpen(false)
                    setUserId('')
                    fetchResidents(selectedApartmentId)
                } else if (response.status === 401) {
                    toast.error('Nieautoryzowany: Nie posiadasz uprawnień do tej opcji')
                } else {
                    const errorData = await response.json()
                    toast.error(`Błąd: ${errorData.message || 'Nie udało się przypisać użytkownika do mieszkania'}`)
                }
            } catch (error) {
                console.error('An error occurred:', error)
                toast.error('Wystąpił nieoczekiwany błąd')
            }
        }
    }

    const handleRemoveUser = async () => {
        if (selectedApartmentId && userId) {
            try {
                const token = sessionStorage.getItem('jwt_accessToken')
                const params = new URLSearchParams({
                    apartmentSignature: selectedApartmentId,
                    userId: userId
                })
                const url = `http://localhost:8444/bwp/hhn/api/v1/apartment/end-possession-history?${params.toString()}`

                const response = await fetch(url, {
                    method: 'PUT',
                    headers: {
                        'Authorization': `Bearer ${token}`
                    }
                })

                if (response.ok) {
                    toast.success('Użytkownik został usunięty z mieszkania pomyślnie')
                    setIsRemoveUserDialogOpen(false)
                    setUserId('')
                    fetchResidents(selectedApartmentId)
                } else if (response.status === 401) {
                    toast.error('Nieautoryzowany: Nie posiadasz uprawnień do tej opcji')
                } else {
                    const errorData = await response.json()
                    toast.error(`Błąd: ${errorData.message || 'Nie udało się usunąć użytkownika z mieszkania'}`)
                }
            } catch (error) {
                console.error('An error occurred:', error)
                toast.error('Wystąpił nieoczekiwany błąd')
            }
        }
    }

    const fetchResidents = async (apartmentSignature: string) => {
        const token = sessionStorage.getItem('jwt_accessToken')
        try {
            const response = await fetch(`http://localhost:8444/bwp/hhn/api/v1/apartment/current-apartment-residents?apartmentSignature=${apartmentSignature}`, {
                headers: {
                    'Authorization': `Bearer ${token}`
                }
            })
            if (response.ok) {
                const data: UserResponse[] = await response.json()
                setResidents(data)
            } else {
                console.error('Failed to fetch residents')
                toast.error('Failed to fetch residents')
            }
        } catch (error) {
            console.error('An error occurred:', error)
            toast.error('An error occurred while fetching residents')
        }
    }

    const toggleExpand = (apartmentSignature: string) => {
        if (expandedApartment === apartmentSignature) {
            setExpandedApartment(null)
        } else {
            setExpandedApartment(apartmentSignature)
            fetchResidents(apartmentSignature)
        }
    }

    return (
        <Card>
            <CardHeader>
                <CardTitle className="text-2xl font-bold flex items-center space-x-2">
                    <Home className="h-6 w-6 text-primary"/>
                    <span>Zarządzanie Mieszkaniami</span>
                </CardTitle>
            </CardHeader>
            <CardContent>
                <div className="flex justify-end items-center mb-4">
                    <Dialog open={isAddApartmentDialogOpen} onOpenChange={setIsAddApartmentDialogOpen}>
                        <DialogTrigger asChild>
                            <Button>
                                <Plus className="mr-2 h-4 w-4"/> Dodaj Mieszkanie
                            </Button>
                        </DialogTrigger>
                        <DialogContent className="sm:max-w-[425px]">
                            <DialogHeader>
                                <DialogTitle>Dodaj Nowe Mieszkanie</DialogTitle>
                                <DialogDescription>
                                    Wprowadź dane nowego mieszkania. Kliknij zapisz, gdy skończysz.
                                </DialogDescription>
                            </DialogHeader>
                            <div className="grid gap-4 py-4">
                                <div className="grid grid-cols-4 items-center gap-4">
                                    <Label htmlFor="address" className="text-right">
                                        Adres
                                    </Label>
                                    <Input
                                        id="address"
                                        value={newApartment.address}
                                        onChange={(e) => setNewApartment({...newApartment, address: e.target.value})}
                                        className="col-span-3"
                                    />
                                </div>
                                <div className="grid grid-cols-4 items-center gap-4">
                                    <Label htmlFor="city" className="text-right">
                                        Miasto
                                    </Label>
                                    <Input
                                        id="city"
                                        value={newApartment.city}
                                        onChange={(e) => setNewApartment({...newApartment, city: e.target.value})}
                                        className="col-span-3"
                                    />
                                </div>
                                <div className="grid grid-cols-4 items-center gap-4">
                                    <Label htmlFor="zipCode" className="text-right">
                                        Kod Pocztowy
                                    </Label>
                                    <Input
                                        id="zipCode"
                                        value={newApartment.zipCode}
                                        onChange={(e) => setNewApartment({...newApartment, zipCode: e.target.value})}
                                        className="col-span-3"
                                    />
                                </div>
                                <div className="grid grid-cols-4 items-center gap-4">
                                    <Label htmlFor="apartmentArea" className="text-right">
                                        Powierzchnia
                                    </Label>
                                    <Input
                                        id="apartmentArea"
                                        type="number"
                                        step="0.01"
                                        value={newApartment.apartmentArea}
                                        onChange={(e) => setNewApartment({
                                            ...newApartment,
                                            apartmentArea: parseFloat(e.target.value)
                                        })}
                                        className="col-span-3"
                                    />
                                </div>
                                <div className="grid grid-cols-4 items-center gap-4">
                                    <Label htmlFor="apartmentSignature" className="text-right">
                                        Sygnatura
                                    </Label>
                                    <Input
                                        id="apartmentSignature"
                                        value={newApartment.apartmentSignature}
                                        onChange={(e) => setNewApartment({
                                            ...newApartment,
                                            apartmentSignature: e.target.value
                                        })}
                                        className="col-span-3"
                                    />
                                </div>
                                <div className="grid grid-cols-4 items-center gap-4">
                                    <Label htmlFor="apartmentPercentValue" className="text-right">
                                        Wartość %
                                    </Label>
                                    <Input
                                        id="apartmentPercentValue"
                                        type="number"
                                        step="0.01"
                                        value={newApartment.apartmentPercentValue}
                                        onChange={(e) => setNewApartment({
                                            ...newApartment,
                                            apartmentPercentValue: parseFloat(e.target.value)
                                        })}
                                        className="col-span-3"
                                    />
                                </div>
                            </div>
                            <DialogFooter>
                                <Button type="submit" onClick={handleAddApartment}>Zapisz</Button>
                            </DialogFooter>
                        </DialogContent>
                    </Dialog>
                </div>
                <Table>
                    <TableHeader>
                        <TableRow>
                            <TableHead>Adres</TableHead>
                            <TableHead>Miasto</TableHead>
                            <TableHead>Kod Pocztowy</TableHead>
                            <TableHead>Powierzchnia</TableHead>
                            <TableHead>Sygnatura</TableHead>
                            <TableHead>Wartość %</TableHead>
                            <TableHead>Utworzono</TableHead>
                            <TableHead>Zaktualizowano</TableHead>
                            <TableHead>Akcje</TableHead>
                        </TableRow>
                    </TableHeader>
                    <TableBody>
                        {apartments.map((apartment) => (
                            <React.Fragment key={apartment.apartmentId}>
                                <TableRow>
                                    <TableCell>{apartment.address}</TableCell>
                                    <TableCell>{apartment.city}</TableCell>
                                    <TableCell>{apartment.zipCode}</TableCell>
                                    <TableCell>{apartment.apartmentArea.toFixed(2)} m²</TableCell>
                                    <TableCell>{apartment.apartmentSignature}</TableCell>
                                    <TableCell>{apartment.apartmentPercentValue.toFixed(2)}%</TableCell>
                                    <TableCell>{format(parseISO(apartment.createdAt), 'MMMM d, yyyy HH:mm:ss')}</TableCell>
                                    <TableCell>{format(parseISO(apartment.updatedAt), 'MMMM d, yyyy HH:mm:ss')}</TableCell>
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
                                                <DropdownMenuItem onClick={() => handleEdit(apartment)}>
                                                    Edytuj Mieszkanie
                                                </DropdownMenuItem>
                                                <DropdownMenuItem onClick={() => {
                                                    setSelectedApartmentId(apartment.apartmentSignature)
                                                    setIsAssignUserDialogOpen(true)
                                                }}>
                                                    <UserPlus className="mr-2 h-4 w-4"/>
                                                    Przypisz Użytkownika
                                                </DropdownMenuItem>
                                                <DropdownMenuItem onClick={() => {
                                                    setSelectedApartmentId(apartment.apartmentSignature)
                                                    setIsRemoveUserDialogOpen(true)
                                                }}>
                                                    <UserMinus className="mr-2 h-4 w-4"/>
                                                    Usuń Przypisanie
                                                </DropdownMenuItem>
                                                <DropdownMenuSeparator/>
                                                <DropdownMenuItem
                                                    onClick={() => handleDelete(apartment.apartmentSignature)}
                                                    className="text-red-600"
                                                >
                                                    <Trash2 className="mr-2 h-4 w-4"/>
                                                    Usuń Mieszkanie
                                                </DropdownMenuItem>
                                            </DropdownMenuContent>
                                        </DropdownMenu>
                                    </TableCell>
                                </TableRow>
                                <TableRow>
                                    <TableCell colSpan={9}>
                                        <Collapsible>
                                            <CollapsibleTrigger asChild>
                                                <Button
                                                    variant="ghost"
                                                    size="sm"
                                                    onClick={() => toggleExpand(apartment.apartmentSignature)}
                                                >
                                                    {expandedApartment === apartment.apartmentSignature ? (
                                                        <>
                                                            <ChevronUp className="h-4 w-4 mr-2"/>
                                                            Ukryj mieszkańców
                                                        </>
                                                    ) : (
                                                        <>
                                                            <ChevronDown className="h-4 w-4 mr-2"/>
                                                            Pokaż mieszkańców
                                                        </>
                                                    )}
                                                </Button>
                                            </CollapsibleTrigger>
                                            <CollapsibleContent>
                                                {expandedApartment === apartment.apartmentSignature && (
                                                    <Card className="mt-2">
                                                        <CardHeader>
                                                            <CardTitle className="text-lg">Mieszkańcy</CardTitle>
                                                        </CardHeader>
                                                        <CardContent>
                                                            {residents.length > 0 ? (
                                                                <Table>
                                                                    <TableHeader>
                                                                        <TableRow>
                                                                            <TableHead>Imię</TableHead>
                                                                            <TableHead>Nazwisko</TableHead>
                                                                            <TableHead>Email</TableHead>
                                                                        </TableRow>
                                                                    </TableHeader>
                                                                    <TableBody>
                                                                        {residents.map((resident, index) => (
                                                                            <TableRow key={index}>
                                                                                <TableCell>{resident.firstName}</TableCell>
                                                                                <TableCell>{resident.lastName}</TableCell>
                                                                                <TableCell>{resident.email}</TableCell>
                                                                            </TableRow>
                                                                        ))}
                                                                    </TableBody>
                                                                </Table>
                                                            ) : (
                                                                <div className="text-center py-4">
                                                                    <Users
                                                                        className="h-8 w-8 text-muted-foreground mx-auto mb-2"/>
                                                                    <p>Brak przypisanych mieszkańców</p>
                                                                </div>
                                                            )}
                                                        </CardContent>
                                                    </Card>
                                                )}
                                            </CollapsibleContent>
                                        </Collapsible>
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
            <Dialog open={!!editingApartment} onOpenChange={() => setEditingApartment(null)}>
                <DialogContent className="sm:max-w-[425px]">
                    <DialogHeader>
                        <DialogTitle>Edytuj Mieszkanie</DialogTitle>
                        <DialogDescription>
                            Wprowadź nowe dane mieszkania. Kliknij zapisz, gdy skończysz.
                        </DialogDescription>
                    </DialogHeader>
                    {editingApartment && (
                        <div className="grid gap-4 py-4">
                            <div className="grid grid-cols-4 items-center gap-4">
                                <Label htmlFor="edit-address" className="text-right">
                                    Adres
                                </Label>
                                <Input
                                    id="edit-address"
                                    value={editingApartment.address}
                                    onChange={(e) => setEditingApartment({
                                        ...editingApartment,
                                        address: e.target.value
                                    })}
                                    className="col-span-3"
                                />
                            </div>
                            <div className="grid grid-cols-4 items-center gap-4">
                                <Label htmlFor="edit-city" className="text-right">
                                    Miasto
                                </Label>
                                <Input
                                    id="edit-city"
                                    value={editingApartment.city}
                                    onChange={(e) => setEditingApartment({...editingApartment, city: e.target.value})}
                                    className="col-span-3"
                                />
                            </div>
                            <div className="grid grid-cols-4 items-center gap-4">
                                <Label htmlFor="edit-zipCode" className="text-right">
                                    Kod Pocztowy
                                </Label>
                                <Input
                                    id="edit-zipCode"
                                    value={editingApartment.zipCode}
                                    onChange={(e) => setEditingApartment({
                                        ...editingApartment,
                                        zipCode: e.target.value
                                    })}
                                    className="col-span-3"
                                />
                            </div>
                            <div className="grid grid-cols-4 items-center gap-4">
                                <Label htmlFor="edit-apartmentArea" className="text-right">
                                    Powierzchnia
                                </Label>
                                <Input
                                    id="edit-apartmentArea"
                                    type="number"
                                    step="0.01"
                                    value={editingApartment.apartmentArea}
                                    onChange={(e) => setEditingApartment({
                                        ...editingApartment,
                                        apartmentArea: parseFloat(e.target.value)
                                    })}
                                    className="col-span-3"
                                />
                            </div>
                            <div className="grid grid-cols-4 items-center gap-4">
                                <Label htmlFor="edit-apartmentSignature" className="text-right">
                                    Sygnatura
                                </Label>
                                <Input
                                    id="edit-apartmentSignature"
                                    value={editingApartment.apartmentSignature}
                                    onChange={(e) => setEditingApartment({
                                        ...editingApartment,
                                        apartmentSignature: e.target.value
                                    })}
                                    className="col-span-3"
                                />
                            </div>
                            <div className="grid grid-cols-4 items-center gap-4">
                                <Label htmlFor="edit-apartmentPercentValue" className="text-right">
                                    Wartość %
                                </Label>
                                <Input
                                    id="edit-apartmentPercentValue"
                                    type="number"
                                    step="0.01"
                                    value={editingApartment.apartmentPercentValue}
                                    onChange={(e) => setEditingApartment({
                                        ...editingApartment,
                                        apartmentPercentValue: parseFloat(e.target.value)
                                    })}
                                    className="col-span-3"
                                />
                            </div>
                        </div>
                    )}
                    <DialogFooter>
                        <Button type="submit" onClick={handleSaveEdit}>Zapisz</Button>
                    </DialogFooter>
                </DialogContent>
            </Dialog>
            <Dialog open={isAssignUserDialogOpen} onOpenChange={setIsAssignUserDialogOpen}>
                <DialogContent className="sm:max-w-[425px]">
                    <DialogHeader>
                        <DialogTitle>Przypisz Użytkownika do Mieszkania</DialogTitle>
                        <DialogDescription>
                            Wprowadź ID użytkownika, którego chcesz przypisać do tego mieszkania.
                        </DialogDescription>
                    </DialogHeader>
                    <div className="grid gap-4 py-4">
                        <div className="grid grid-cols-4 items-center gap-4">
                            <Label htmlFor="userId" className="text-right">
                                ID Użytkownika
                            </Label>
                            <Input
                                id="userId"
                                value={userId}
                                onChange={(e) => setUserId(e.target.value)}
                                className="col-span-3"
                            />
                        </div>
                    </div>
                    <DialogFooter>
                        <Button type="submit" onClick={handleAssignUser}>Przypisz</Button>
                    </DialogFooter>
                </DialogContent>
            </Dialog>
            <Dialog open={isRemoveUserDialogOpen} onOpenChange={setIsRemoveUserDialogOpen}>
                <DialogContent className="sm:max-w-[425px]">
                    <DialogHeader>
                        <DialogTitle>Usuń Przypisanie Użytkownika z Mieszkania</DialogTitle>
                        <DialogDescription>
                            Wprowadź ID użytkownika, którego chcesz usunąć z tego mieszkania.
                        </DialogDescription>
                    </DialogHeader>
                    <div className="grid gap-4 py-4">
                        <div className="grid grid-cols-4 items-center gap-4">
                            <Label htmlFor="removeUserId" className="text-right">
                                ID Użytkownika
                            </Label>
                            <Input
                                id="removeUserId"
                                value={userId}
                                onChange={(e) => setUserId(e.target.value)}
                                className="col-span-3"
                            />
                        </div>
                    </div>
                    <DialogFooter>
                        <Button type="submit" onClick={handleRemoveUser}>Usuń Przypisanie</Button>
                    </DialogFooter>
                </DialogContent>
            </Dialog>
        </Card>
    )
}