'use client'

import React, {useState, useEffect} from 'react'
import {
    Table,
    TableBody,
    TableCell,
    TableHead,
    TableHeader,
    TableRow,
} from "@/components/ui/table"
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
    Select,
    SelectContent,
    SelectItem,
    SelectTrigger,
    SelectValue,
} from "@/components/ui/select"
import {ChevronLeft, ChevronRight, MoreHorizontal, Plus, Trash2, Briefcase} from 'lucide-react'
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

const roles = ['ROLE_ADMIN', 'ROLE_EMPLOYEE', 'ROLE_OWNER']

export function UsersManagement() {
    const [users, setUsers] = useState<User[]>([])
    const [currentPage, setCurrentPage] = useState(0)
    const [totalPages, setTotalPages] = useState(0)
    const [searchTerm, setSearchTerm] = useState('')
    const [selectedRole, setSelectedRole] = useState('All')
    const [sortColumn, setSortColumn] = useState<keyof User>('firstName')
    const [sortDirection, setSortDirection] = useState<'asc' | 'desc'>('asc')
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

    // Function to fetch users from the server
    const fetchUsers = async () => {

        // Get current user's email from the JWT token
        const token = localStorage.getItem('jwt_accessToken')
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
                // Exclude the current user from the list
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

    const handleSort = (column: keyof User) => {
        if (column === sortColumn) {
            setSortDirection(sortDirection === 'asc' ? 'desc' : 'asc')
        } else {
            setSortColumn(column)
            setSortDirection('asc')
        }
    }

    const filteredUsers = users
        .filter(user =>
            (`${user.firstName} ${user.lastName}`.toLowerCase().includes(searchTerm.toLowerCase()) ||
                user.email.toLowerCase().includes(searchTerm.toLowerCase())) &&
            (selectedRole === 'All' || user.role === selectedRole)
        )
        .sort((a, b) => {
            const aValue = a[sortColumn]
            const bValue = b[sortColumn]
            if (aValue < bValue) return sortDirection === 'asc' ? -1 : 1
            if (aValue > bValue) return sortDirection === 'asc' ? 1 : -1
            return 0
        })

    const handleEdit = (user: User) => {
        setEditingUser(user)
    }

    const handleSaveEdit = async () => {
        if (editingUser) {
            try {
                const token = localStorage.getItem('jwt_accessToken')
                // Construct the URL with query parameters
                const params = new URLSearchParams({
                    userId: editingUser.id,
                    accessToken: token || ''
                })
                const url = `http://localhost:8444/bwp/hhn/api/v1/user/update-user-by-id?${params.toString()}`

                // Prepare the userRequest object
                const validatedData = registerSchema.parse({
                    firstName: newUser.firstName,
                    lastName: newUser.lastName,
                    email: newUser.email,
                    password: newUser.password,
                    phoneNumber: newUser.phoneNumber,
                    role: newUser.role,
                })

                // Send PUT request to update user
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

                    // Close dialog
                    setEditingUser(null)

                    // Refresh the user list
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
            const token = localStorage.getItem('jwt_accessToken')
            // Construct the URL with query parameters
            const params = new URLSearchParams({
                userId: userId,
                accessToken: token || ''
            })
            const url = `http://localhost:8444/bwp/hhn/api/v1/user/delete-user-by-id?${params.toString()}`

            // Send DELETE request to delete user
            const response = await fetch(url, {
                method: 'DELETE',
                headers: {
                    'Authorization': `Bearer ${token}`
                }
            })

            if (response.ok) {
                toast.success('Użytkownik został usunięty pomyślnie')

                // Refresh the user list
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
        phoneNumber: z.string().regex(/^\d{9,11}$/, 'Invalid phone number format').optional(),
        role: z.enum(['ROLE_ADMIN', 'ROLE_EMPLOYEE', 'ROLE_OWNER']),
    })

    const handleAddUser = async () => {
        try {
            // Validate form data
            const validatedData = registerSchema.parse({
                firstName: newUser.firstName,
                lastName: newUser.lastName,
                email: newUser.email,
                password: newUser.password,
                phoneNumber: newUser.phoneNumber,
                role: newUser.role,
            })

            const token = localStorage.getItem('jwt_accessToken')

            // Send POST request to register endpoint
            const response = await fetch(`http://localhost:8444/bwp/hhn/api/v1/auth/register?accessToken=${token}`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${token}`
                },
                body: JSON.stringify(validatedData)
            })

            if (response.ok) {
                // User registered successfully
                toast.success('Użytkownik został dodany pomyślnie')

                // Close dialog and reset form
                setIsAddUserDialogOpen(false)
                setNewUser({ firstName: '', lastName: '', email: '', phoneNumber: '', role: 'ROLE_OWNER', password: '' })

                // Refresh the user list
                fetchUsers()
            } else if (response.status === 401) {
                // Unauthorized
                toast.error('Nieautoryzowany: Nie posiadasz uprawnień do tej opcji')
            } else {
                // Other errors
                const errorData = await response.json()
                toast.error(`Błąd: ${errorData.message || 'Nie udało się dodać użytkownika'}`)
            }
        } catch (error) {
            if (error instanceof z.ZodError) {
                // Validation errors
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
                <div className="flex justify-between items-center mb-4">
                    <div className="flex items-center space-x-2">
                        <Input
                            placeholder="Szukaj użytkowników..."
                            value={searchTerm}
                            onChange={(e) => setSearchTerm(e.target.value)}
                            className="w-64"
                        />
                        <Select value={selectedRole} onValueChange={setSelectedRole}>
                            <SelectTrigger className="w-[180px]">
                                <SelectValue placeholder="Wybierz rolę"/>
                            </SelectTrigger>
                            <SelectContent>
                                <SelectItem value="All">All</SelectItem>
                                {roles.map((role) => (
                                    <SelectItem key={role} value={role}>
                                        {role}
                                    </SelectItem>
                                ))}
                            </SelectContent>
                        </Select>
                    </div>
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
                            <TableHead onClick={() => handleSort('id')} className="cursor-pointer">
                                ID {sortColumn === 'id' && (sortDirection === 'asc' ? '▲' : '▼')}
                            </TableHead>
                            <TableHead onClick={() => handleSort('firstName')} className="cursor-pointer">
                                Imię {sortColumn === 'firstName' && (sortDirection === 'asc' ? '▲' : '▼')}
                            </TableHead>
                            <TableHead onClick={() => handleSort('lastName')} className="cursor-pointer">
                                Nazwisko {sortColumn === 'lastName' && (sortDirection === 'asc' ? '▲' : '▼')}
                            </TableHead>
                            <TableHead onClick={() => handleSort('email')} className="cursor-pointer">
                                Email {sortColumn === 'email' && (sortDirection === 'asc' ? '▲' : '▼')}
                            </TableHead>
                            <TableHead onClick={() => handleSort('phoneNumber')} className="cursor-pointer">
                                Numer Telefonu {sortColumn === 'phoneNumber' && (sortDirection === 'asc' ? '▲' : '▼')}
                            </TableHead>
                            <TableHead onClick={() => handleSort('role')} className="cursor-pointer">
                                Rola {sortColumn === 'role' && (sortDirection === 'asc' ? '▲' : '▼')}
                            </TableHead>
                            <TableHead onClick={() => handleSort('createdAt')} className="cursor-pointer">
                                Utworzono {sortColumn === 'createdAt' && (sortDirection === 'asc' ? '▲' : '▼')}
                            </TableHead>
                            <TableHead onClick={() => handleSort('updatedAt')} className="cursor-pointer">
                                Zaktualizowano {sortColumn === 'updatedAt' && (sortDirection === 'asc' ? '▲' : '▼')}
                            </TableHead>
                            <TableHead>Akcje</TableHead>
                        </TableRow>
                    </TableHeader>
                    <TableBody>
                        {filteredUsers.map((user) => (
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

                <div className="flex items-center justify-end space-x-2 py-4">
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
