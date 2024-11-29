'use client'

import React, {useEffect, useState} from 'react'
import {Table, TableBody, TableCell, TableHead, TableHeader, TableRow} from "@/components/ui/table"
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
import {Select, SelectContent, SelectItem, SelectTrigger, SelectValue} from "@/components/ui/select"
import {Briefcase, ChevronLeft, ChevronRight, MoreHorizontal, Plus, Trash2} from 'lucide-react'
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
import {jwtDecode} from "jwt-decode"

interface User {
    id: string
    firstName: string
    lastName: string
    email: string
    phoneNumber: string
    role: string
    password?: string
    createdAt: string
    updatedAt: string
    originalRole?: string // New field to store the original role
}

interface UserResponse {
    userId: string
    firstName: string
    lastName: string
    email: string
    phoneNumber: string
    createdAt: string
    updatedAt: string
    role: string
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

interface CustomJwtPayload {
    userId: string;
    sub: string;
}

const roles = ['ROLE_SUPER_ADMIN', 'ROLE_ADMIN', 'ROLE_EMPLOYEE', 'ROLE_OWNER']

export function UsersManagement() {
    const [users, setUsers] = useState<User[]>([])
    const [currentPage, setCurrentPage] = useState(0)
    const [totalPages, setTotalPages] = useState(0)
    const [editingUser, setEditingUser] = useState<User | null>(null)
    const [isAddUserDialogOpen, setIsAddUserDialogOpen] = useState(false)
    const [newUser, setNewUser] = useState<Omit<User, 'id' | 'createdAt' | 'updatedAt'> & { password: string }>({
        firstName: '',
        lastName: '',
        email: '',
        phoneNumber: '',
        role: 'ROLE_OWNER',
        password: ''
    })

    const fetchUsers = async () => {
        const token = sessionStorage.getItem('jwt_accessToken')
        let currentUserEmail = ''
        if (token) {
            const decodedToken = jwtDecode<CustomJwtPayload>(token)
            currentUserEmail = decodedToken.sub
        }

        try {
            const response = await fetch(`http://localhost:8444/bwp/hhn/api/v1/user/get-all-users?pageNo=${currentPage}&pageSize=10`, {
                headers: {
                    'Authorization': `Bearer ${token}`
                }
            })
            if (response.ok) {
                const data: PageResponse<UserResponse> = await response.json()
                const usersData = data.content
                    .filter(user => user.email !== currentUserEmail)
                    .map(user => ({
                        id: user.userId,
                        firstName: user.firstName,
                        lastName: user.lastName,
                        email: user.email,
                        phoneNumber: user.phoneNumber,
                        role: user.role,
                        createdAt: user.createdAt,
                        updatedAt: user.updatedAt,
                        originalRole: user.role, // Store the original role
                    }))
                setUsers(usersData)
                setTotalPages(data.totalPages)
            } else {
                console.error('Failed to fetch users')
            }
        } catch (error) {
            console.error('An error occurred:', error)
        }
    }

    useEffect(() => {
        fetchUsers()
    }, [currentPage])

    const handleEdit = (user: User) => {
        setEditingUser({...user, originalRole: user.originalRole || user.role})
    }

    const saveUserSchema = z.object({
        firstName: z.string().min(3, "String must contain at least 3 character(s)"),
        lastName: z.string().min(3, "String must contain at least 3 character(s)"),
        email: z.string().email("Invalid email"),
        password: z.string().min(8, "String must contain at least 8 character(s)").optional(),
        phoneNumber: z.string().regex(/^\d{9,11}$/, "Invalid phone number format"),
        role: z.enum(['ROLE_ADMIN', 'ROLE_EMPLOYEE', 'ROLE_OWNER']),
    });

    const handleSaveEdit = async () => {
        if (editingUser) {
            try {
                const token = sessionStorage.getItem('jwt_accessToken')
                const params = new URLSearchParams({
                    userId: editingUser.id,
                    accessToken: token || ''
                })
                const url = `http://localhost:8444/bwp/hhn/api/v1/user/update-user-by-id?${params.toString()}`

                // Check if the role can be changed
                if (editingUser.originalRole === 'ROLE_OWNER' && editingUser.role !== 'ROLE_OWNER') {
                    toast.error('Nie można zmienić roli właściciela')
                    return
                }

                if (editingUser.originalRole !== 'ROLE_OWNER' && editingUser.role === 'ROLE_OWNER') {
                    toast.error('Nie można zmienić roli na właściciela')
                    return
                }

                const validatedData = saveUserSchema.parse({
                    firstName: editingUser.firstName,
                    lastName: editingUser.lastName,
                    email: editingUser.email,
                    password: editingUser.password,
                    phoneNumber: editingUser.phoneNumber,
                    role: editingUser.role,
                })

                const response = await fetch(url, {
                    method: 'PUT',
                    headers: {
                        'Content-Type': 'application/json',
                        'Authorization': `Bearer ${token}`
                    },
                    body: JSON.stringify(validatedData)
                })

                if (response.ok) {
                    toast.success('Użytkownik został zaktualizowany pomyślnie')
                    setEditingUser(null)
                    fetchUsers()
                } else if (response.status === 401) {
                    toast.error('Nieautoryzowany: Nie posiadasz uprawnień do tej opcji')
                } else {
                    const errorData = await response.json()
                    toast.error(`Błąd: ${errorData.message || 'Nie udało się zaktualizować użytkownika'}`)
                }
            } catch (error) {
                console.error('An error occurred:', error)
                toast.error('Wystąpił nieoczekiwany błąd')
            }
        }
    }

    const handleDelete = async (userId: string) => {
        try {
            const token = sessionStorage.getItem('jwt_accessToken')
            const params = new URLSearchParams({
                userId: userId,
                accessToken: token || ''
            })
            const url = `http://localhost:8444/bwp/hhn/api/v1/user/delete-user-by-id?${params.toString()}`

            const response = await fetch(url, {
                method: 'DELETE',
                headers: {
                    'Authorization': `Bearer ${token}`
                }
            })

            if (response.ok) {
                toast.success('Użytkownik został usunięty pomyślnie')
                fetchUsers()
            } else if (response.status === 401) {
                toast.error('Nieautoryzowany: Nie posiadasz uprawnień do tej opcji')
            } else {
                const errorData = await response.json()
                toast.error(`Błąd: ${errorData.message || 'Nie udało się usunąć użytkownika'}`)
            }
        } catch (error) {
            console.error('An error occurred:', error)
            toast.error('Wystąpił nieoczekiwany błąd')
        }
    }

    const registerSchema = z.object({
        firstName: z.string().min(3).max(50),
        lastName: z.string().min(3).max(50),
        email: z.string().email(),
        password: z.string().min(8).max(255),
        phoneNumber: z.string().regex(/^\d{9,11}$/, 'Invalid phone number format'),
        role: z.enum(['ROLE_ADMIN', 'ROLE_EMPLOYEE', 'ROLE_OWNER']),
    })

    const handleAddUser = async () => {
        try {
            const validatedData = registerSchema.parse({
                firstName: newUser.firstName,
                lastName: newUser.lastName,
                email: newUser.email,
                password: newUser.password,
                phoneNumber: newUser.phoneNumber,
                role: newUser.role,
            })

            const token = sessionStorage.getItem('jwt_accessToken')

            const response = await fetch(`http://localhost:8444/bwp/hhn/api/v1/auth/register?accessToken=${token}`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${token}`
                },
                body: JSON.stringify(validatedData)
            })

            if (response.ok) {
                toast.success('Użytkownik został dodany pomyślnie')
                setIsAddUserDialogOpen(false)
                setNewUser({firstName: '', lastName: '', email: '', phoneNumber: '', role: 'ROLE_OWNER', password: ''})
                fetchUsers()
            } else if (response.status === 401) {
                toast.error('Nieautoryzowany: Nie posiadasz uprawnień do tej opcji')
            } else {
                const errorData = await response.json()
                toast.error(`Błąd: ${errorData.message || 'Nie udało się dodać użytkownika'}`)
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

    return (
        <Card>
            <CardHeader>
                <CardTitle className="text-2xl font-bold flex items-center space-x-2">
                    <Briefcase className="h-6 w-6 text-primary"/>
                    <span>Zarządzanie Użytkownikami</span>
                </CardTitle>
            </CardHeader>
            <CardContent>
                <div className="flex justify-end items-center mb-4">
                    <Dialog open={isAddUserDialogOpen} onOpenChange={setIsAddUserDialogOpen}>
                        <DialogTrigger asChild>
                            <Button>
                                <Plus className="mr-2 h-4 w-4"/> Dodaj Użytkownika
                            </Button>
                        </DialogTrigger>
                        <DialogContent className="sm:max-w-[425px]">
                            <DialogHeader>
                                <DialogTitle>Dodaj Nowego Użytkownika</DialogTitle>
                                <DialogDescription>
                                    Wprowadź dane nowego użytkownika. Kliknij zapisz, gdy skończysz.
                                </DialogDescription>
                            </DialogHeader>
                            <div className="grid gap-4 py-4">
                                <div className="grid grid-cols-4 items-center gap-4">
                                    <Label htmlFor="firstName" className="text-right">
                                        Imię
                                    </Label>
                                    <Input
                                        id="firstName"
                                        value={newUser.firstName}
                                        onChange={(e) => setNewUser({...newUser, firstName: e.target.value})}
                                        className="col-span-3"
                                    />
                                </div>
                                <div className="grid grid-cols-4 items-center gap-4">
                                    <Label htmlFor="lastName" className="text-right">
                                        Nazwisko
                                    </Label>
                                    <Input
                                        id="lastName"
                                        value={newUser.lastName}
                                        onChange={(e) => setNewUser({...newUser, lastName: e.target.value})}
                                        className="col-span-3"
                                    />
                                </div>
                                <div className="grid grid-cols-4 items-center gap-4">
                                    <Label htmlFor="email" className="text-right">
                                        Email
                                    </Label>
                                    <Input
                                        id="email"
                                        value={newUser.email}
                                        onChange={(e) => setNewUser({...newUser, email: e.target.value})}
                                        className="col-span-3"
                                    />
                                </div>
                                <div className="grid grid-cols-4 items-center gap-4">
                                    <Label htmlFor="phoneNumber" className="text-right">
                                        Numer Telefonu
                                    </Label>
                                    <Input
                                        id="phoneNumber"
                                        value={newUser.phoneNumber}
                                        onChange={(e) => setNewUser({...newUser, phoneNumber: e.target.value})}
                                        className="col-span-3"
                                    />
                                </div>
                                <div className="grid grid-cols-4 items-center gap-4">
                                    <Label htmlFor="role" className="text-right">
                                        Rola
                                    </Label>
                                    <Select
                                        value={newUser.role}
                                        onValueChange={(value) => setNewUser({...newUser, role: value})}
                                    >
                                        <SelectTrigger className="col-span-3">
                                            <SelectValue placeholder="Wybierz rolę"/>
                                        </SelectTrigger>
                                        <SelectContent>
                                            {roles.map((role) => (
                                                <SelectItem key={role} value={role}>
                                                    {role}
                                                </SelectItem>
                                            ))}
                                        </SelectContent>
                                    </Select>
                                </div>
                                <div className="grid grid-cols-4 items-center gap-4">
                                    <Label htmlFor="password" className="text-right">
                                        Hasło
                                    </Label>
                                    <Input
                                        id="password"
                                        type="password"
                                        value={newUser.password}
                                        onChange={(e) => setNewUser({...newUser, password: e.target.value})}
                                        className="col-span-3"
                                    />
                                </div>
                            </div>
                            <DialogFooter>
                                <Button type="submit" onClick={handleAddUser}>Zapisz</Button>
                            </DialogFooter>
                        </DialogContent>
                    </Dialog>
                </div>
                <Table>
                    <TableHeader>
                        <TableRow>
                            <TableHead>ID</TableHead>
                            <TableHead>Imię</TableHead>
                            <TableHead>Nazwisko</TableHead>
                            <TableHead>Email</TableHead>
                            <TableHead>Numer Telefonu</TableHead>
                            <TableHead>Rola</TableHead>
                            <TableHead>Utworzono</TableHead>
                            <TableHead>Zaktualizowano</TableHead>
                            <TableHead>Akcje</TableHead>
                        </TableRow>
                    </TableHeader>
                    <TableBody>
                        {users.map((user) => (
                            <TableRow key={user.id}>
                                <TableCell>{user.id}</TableCell>
                                <TableCell>{user.firstName}</TableCell>
                                <TableCell>{user.lastName}</TableCell>
                                <TableCell>{user.email}</TableCell>
                                <TableCell>{user.phoneNumber}</TableCell>
                                <TableCell>{user.role}</TableCell>
                                <TableCell>{format(parseISO(user.createdAt), 'MMMM d, yyyy HH:mm:ss')}</TableCell>
                                <TableCell>{format(parseISO(user.updatedAt), 'MMMM d, yyyy HH:mm:ss')}</TableCell>
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
                                            <DropdownMenuItem onClick={() => handleEdit(user)}>
                                                Edytuj Użytkownika
                                            </DropdownMenuItem>
                                            <DropdownMenuSeparator/>
                                            <DropdownMenuItem
                                                onClick={() => handleDelete(user.id)}
                                                className="text-red-600"
                                            >
                                                <Trash2 className="mr-2 h-4 w-4"/>
                                                Usuń Użytkownika
                                            </DropdownMenuItem>
                                        </DropdownMenuContent>
                                    </DropdownMenu>
                                </TableCell>
                            </TableRow>
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
            <Dialog open={!!editingUser} onOpenChange={() => setEditingUser(null)}>
                <DialogContent className="sm:max-w-[425px]">
                    <DialogHeader>
                        <DialogTitle>Edytuj Użytkownika</DialogTitle>
                        <DialogDescription>
                            Wprowadź nowe dane użytkownika. Kliknij zapisz, gdy skończysz.
                        </DialogDescription>
                    </DialogHeader>
                    {editingUser && (
                        <div className="grid gap-4 py-4">
                            <div className="grid grid-cols-4 items-center gap-4">
                                <Label htmlFor="edit-firstName" className="text-right">
                                    Imię
                                </Label>
                                <Input
                                    id="edit-firstName"
                                    value={editingUser.firstName}
                                    onChange={(e) => setEditingUser({...editingUser, firstName: e.target.value})}
                                    className="col-span-3"
                                />
                            </div>
                            <div className="grid grid-cols-4 items-center gap-4">
                                <Label htmlFor="edit-lastName" className="text-right">
                                    Nazwisko
                                </Label>
                                <Input
                                    id="edit-lastName"
                                    value={editingUser.lastName}
                                    onChange={(e) => setEditingUser({...editingUser, lastName: e.target.value})}
                                    className="col-span-3"
                                />
                            </div>
                            <div className="grid grid-cols-4 items-center gap-4">
                                <Label htmlFor="edit-email" className="text-right">
                                    Email
                                </Label>
                                <Input
                                    id="edit-email"
                                    value={editingUser.email}
                                    onChange={(e) => setEditingUser({...editingUser, email: e.target.value})}
                                    className="col-span-3"
                                />
                            </div>
                            <div className="grid grid-cols-4 items-center gap-4">
                                <Label htmlFor="edit-phoneNumber" className="text-right">
                                    Numer Telefonu
                                </Label>
                                <Input
                                    id="edit-phoneNumber"
                                    value={editingUser.phoneNumber}
                                    onChange={(e) => setEditingUser({...editingUser, phoneNumber: e.target.value})}
                                    className="col-span-3"
                                />
                            </div>
                            <div className="grid grid-cols-4 items-center gap-4">
                                <Label htmlFor="edit-role" className="text-right">
                                    Rola
                                </Label>
                                <Select
                                    value={editingUser.role}
                                    onValueChange={(value) => setEditingUser({...editingUser, role: value})}
                                    disabled={editingUser.originalRole === 'ROLE_OWNER'}
                                >
                                    <SelectTrigger className="col-span-3">
                                        <SelectValue placeholder="Wybierz rolę"/>
                                    </SelectTrigger>
                                    <SelectContent>
                                        {editingUser.originalRole === 'ROLE_OWNER' ? (
                                            <SelectItem value="ROLE_OWNER">ROLE_OWNER</SelectItem>
                                        ) : editingUser.originalRole === 'ROLE_SUPER_ADMIN' ? (
                                            <SelectItem value="ROLE_SUPER_ADMIN">ROLE_SUPER_ADMIN</SelectItem>
                                        ) : (
                                            ['ROLE_ADMIN', 'ROLE_EMPLOYEE'].map((role) => (
                                                <SelectItem key={role} value={role}>
                                                    {role}
                                                </SelectItem>
                                            ))
                                        )}
                                    </SelectContent>
                                </Select>
                            </div>
                            <div className="grid grid-cols-4 items-center gap-4">
                                <Label htmlFor="edit-password" className="text-right">
                                    Hasło
                                </Label>
                                <Input
                                    id="edit-password"
                                    type="password"
                                    value={editingUser.password || ''}
                                    onChange={(e) => setEditingUser({...editingUser, password: e.target.value})}
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
        </Card>
    )
}