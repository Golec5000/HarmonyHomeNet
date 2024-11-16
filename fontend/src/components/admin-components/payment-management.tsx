'use client'

import React, {useState, useEffect} from 'react'
import {z} from 'zod'
import {useForm} from 'react-hook-form'
import {zodResolver} from '@hookform/resolvers/zod'
import {format} from 'date-fns'
import {toast} from 'sonner'
import {
    Table,
    TableBody,
    TableCell,
    TableHead,
    TableHeader,
    TableRow,
} from "@/components/ui/table"
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
import {Switch} from "@/components/ui/switch"
import {
    Card,
    CardContent,
    CardHeader,
    CardTitle,
} from "@/components/ui/card"
import {Plus, MoreHorizontal, Trash2, Edit, ChevronLeft, ChevronRight, Settings, CreditCard, Home} from 'lucide-react'

// Interfaces
interface Payment {
    paymentId: string
    paymentStatus: 'PENDING' | 'PAID' | 'OVERDUE'
    paymentDate: string
    paymentTime: string
    paymentAmount: number
    createdAt: string
    description: string
    readyToPay: boolean
    apartmentSignature: string
}

interface PaymentComponent {
    id: number
    componentType: string
    unitPrice: number
    specialMultiplier: number
    componentAmount: number
    createdAt: string
    updatedAt: string
    unit: string
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

// Zod Schemas
const paymentSchema = z.object({
    apartmentSignature: z.string().min(1),
    description: z.string().min(1),
})

const paymentComponentSchema = z.object({
    componentType: z.string().min(1).max(50),
    unitPrice: z.number().positive(),
    specialMultiplier: z.number().min(1),
    componentAmount: z.number().positive(),
    unit: z.string().min(1),
})

export function PaymentManagement() {
    const [payments, setPayments] = useState<Payment[]>([])
    const [currentPage, setCurrentPage] = useState(0)
    const [totalPages, setTotalPages] = useState(0)
    const [isAddPaymentDialogOpen, setIsAddPaymentDialogOpen] = useState(false)
    const [isEditPaymentDialogOpen, setIsEditPaymentDialogOpen] = useState(false)
    const [isComponentDialogOpen, setIsComponentDialogOpen] = useState(false)
    const [editingPayment, setEditingPayment] = useState<Payment | null>(null)
    const [paymentComponents, setPaymentComponents] = useState<PaymentComponent[]>([])
    const [editingComponent, setEditingComponent] = useState<PaymentComponent | null>(null)
    const [isAddingComponent, setIsAddingComponent] = useState(false)

    const {
        register: registerPayment,
        handleSubmit: handleSubmitPayment,
        reset: resetPayment,
        formState: {errors: paymentErrors}
    } = useForm<z.infer<typeof paymentSchema>>({
        resolver: zodResolver(paymentSchema),
    })

    const {
        register: registerComponent,
        handleSubmit: handleSubmitComponent,
        reset: resetComponent,
        formState: {errors: componentErrors}
    } = useForm<z.infer<typeof paymentComponentSchema>>({
        resolver: zodResolver(paymentComponentSchema),
    })

    useEffect(() => {
        fetchPayments()
    }, [currentPage])

    useEffect(() => {
        if (editingPayment) {
            resetPayment({
                apartmentSignature: editingPayment.apartmentSignature,
                description: editingPayment.description,
            });
        }
    }, [editingPayment, resetPayment]);

    const fetchPayments = async () => {
        try {
            const response = await fetch(`http://localhost:8444/bwp/hhn/api/v1/payment/get-all-payments?pageNo=${currentPage}&pageSize=10`, {
                headers: {
                    'Authorization': `Bearer ${localStorage.getItem('jwt_accessToken')}`
                }
            })
            if (response.ok) {
                const data: PageResponse<Payment> = await response.json()
                setPayments(data.content)
                setTotalPages(data.totalPages)
            } else {
                toast.error('Failed to fetch payments')
            }
        } catch (error) {
            console.error('Error fetching payments:', error)
            toast.error('An error occurred while fetching payments')
        }
    }

    const fetchPaymentComponents = async (paymentId: string) => {
        try {
            const response = await fetch(`http://localhost:8444/bwp/hhn/api/v1/payment/get-payment-components?paymentId=${paymentId}`, {
                headers: {
                    'Authorization': `Bearer ${localStorage.getItem('jwt_accessToken')}`
                }
            })
            if (response.ok) {
                const data: PaymentComponent[] = await response.json()
                setPaymentComponents(data)
            } else {
                toast.error('Failed to fetch payment components')
            }
        } catch (error) {
            console.error('Error fetching payment components:', error)
            toast.error('An error occurred while fetching payment components')
        }
    }

    const handleCreatePayment = async (data: z.infer<typeof paymentSchema>) => {
        try {
            const response = await fetch('http://localhost:8444/bwp/hhn/api/v1/payment/create-payment', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${localStorage.getItem('jwt_accessToken')}`
                },
                body: JSON.stringify(data)
            })
            if (response.ok) {
                toast.success('Payment created successfully')
                setIsAddPaymentDialogOpen(false)
                resetPayment()
                fetchPayments()
            } else {
                toast.error('Failed to create payment')
            }
        } catch (error) {
            console.error('Error creating payment:', error)
            toast.error('An error occurred while creating the payment')
        }
    }

    const handleUpdatePayment = async (data: z.infer<typeof paymentSchema>) => {
        if (editingPayment) {
            try {
                const params = new URLSearchParams({
                    paymentId: editingPayment.paymentId,
                })
                const response = await fetch(`http://localhost:8444/bwp/hhn/api/v1/payment/update-payment?${params.toString()}`, {
                    method: 'PUT',
                    headers: {
                        'Content-Type': 'application/json',
                        'Authorization': `Bearer ${localStorage.getItem('jwt_accessToken')}`
                    },
                    body: JSON.stringify(data)
                })
                if (response.ok) {
                    toast.success('Payment updated successfully')
                    setIsEditPaymentDialogOpen(false)
                    setEditingPayment(null)
                    resetPayment()
                    fetchPayments()
                } else {
                    toast.error('Failed to update payment')
                }
            } catch (error) {
                console.error('Error updating payment:', error)
                toast.error('An error occurred while updating the payment')
            }
        }
    }

    const handleDeletePayment = async (paymentId: string) => {
        try {
            const response = await fetch(`http://localhost:8444/bwp/hhn/api/v1/payment/delete-payment?paymentId=${paymentId}`, {
                method: 'DELETE',
                headers: {
                    'Authorization': `Bearer ${localStorage.getItem('jwt_accessToken')}`
                }
            })
            if (response.ok) {
                toast.success('Payment deleted successfully')
                fetchPayments()
            } else {
                toast.error('Failed to delete payment')
            }
        } catch (error) {
            console.error('Error deleting payment:', error)
            toast.error('An error occurred while deleting the payment')
        }
    }

    const handleActivatePayment = async (paymentId: string, readyToPay: boolean) => {
        try {
            const params = new URLSearchParams({
                paymentId: paymentId,
                setActive: readyToPay
            })
            const response = await fetch(`http://localhost:8444/bwp/hhn/api/v1/payment/activate-payment?${params.toString()}`, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${localStorage.getItem('jwt_accessToken')}`
                }
            });
            if (response.ok) {
                toast.success('Payment status updated successfully');
                fetchPayments();
            } else {
                toast.error('Failed to update payment status');
            }
        } catch (error) {
            console.error('Error updating payment status:', error);
            toast.error('An error occurred while updating the payment status');
        }
    };

    const handleAddComponent = async (data: z.infer<typeof paymentComponentSchema>) => {
        if (editingPayment) {
            try {
                const response = await fetch(`http://localhost:8444/bwp/hhn/api/v1/payment/add-component-to-payment?paymentId=${editingPayment.paymentId}`, {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                        'Authorization': `Bearer ${localStorage.getItem('jwt_accessToken')}`
                    },
                    body: JSON.stringify(data)
                })
                if (response.ok) {
                    toast.success('Component added successfully')
                    resetComponent()
                    fetchPaymentComponents(editingPayment.paymentId)
                    setIsAddingComponent(false)
                    fetchPayments()
                } else {
                    toast.error('Failed to add component')
                }
            } catch (error) {
                console.error('Error adding component:', error)
                toast.error('An error occurred while adding the component')
            }
        }
    }

    const handleUpdateComponent = async (data: z.infer<typeof paymentComponentSchema>) => {
        if (editingPayment && editingComponent) {
            try {
                console.log(editingComponent.id)
                const response = await fetch(`http://localhost:8444/bwp/hhn/api/v1/payment/update-payment-component/${editingComponent.id}?paymentId=${editingPayment.paymentId}`, {
                    method: 'PUT',
                    headers: {
                        'Content-Type': 'application/json',
                        'Authorization': `Bearer ${localStorage.getItem('jwt_accessToken')}`
                    },
                    body: JSON.stringify(data)
                })
                if (response.ok) {
                    toast.success('Component updated successfully')
                    setEditingComponent(null)
                    resetComponent()
                    fetchPaymentComponents(editingPayment.paymentId)
                    fetchPayments()
                } else {
                    toast.error('Failed to update component')
                }
            } catch (error) {
                console.error('Error updating component:', error)
                toast.error('An error occurred while updating the component')
            }
        }
    }

    const handleDeleteComponent = async (componentId: number) => {
        if (editingPayment) {
            try {
                const response = await fetch(`http://localhost:8444/bwp/hhn/api/v1/payment/remove-component-from-payment/${componentId}?paymentId=${editingPayment.paymentId}`, {
                    method: 'PUT',
                    headers: {
                        'Authorization': `Bearer ${localStorage.getItem('jwt_accessToken')}`
                    }
                })
                if (response.ok) {
                    toast.success('Component deleted successfully')
                    fetchPaymentComponents(editingPayment.paymentId)
                    fetchPayments()
                } else {
                    toast.error('Failed to delete component')
                }
            } catch (error) {
                console.error('Error deleting component:', error)
                toast.error('An error occurred while deleting the component')
            }
        }
    }

    return (
        <Card>
            <CardHeader>
                <CardTitle className="text-2xl font-bold flex items-center space-x-2">
                    <CreditCard className="h-6 w-6 text-primary"/>
                    <span>Zarządzanie Płatnościami</span>
                </CardTitle>
            </CardHeader>
            <CardContent>
                <div className="flex justify-end mb-4">
                    <Dialog open={isAddPaymentDialogOpen} onOpenChange={setIsAddPaymentDialogOpen}>
                        <DialogTrigger asChild>
                            <Button>
                                <Plus className="mr-2 h-4 w-4"/> Dodaj Płatność
                            </Button>
                        </DialogTrigger>
                        <DialogContent className="sm:max-w-[425px]">
                            <DialogHeader>
                                <DialogTitle>Dodaj Nową Płatność</DialogTitle>
                                <DialogDescription>
                                    Wprowadź dane nowej płatności. Kliknij zapisz, gdy skończysz.
                                </DialogDescription>
                            </DialogHeader>
                            <form onSubmit={handleSubmitPayment(handleCreatePayment)}>
                                <div className="grid gap-4 py-4">
                                    <div className="grid grid-cols-4 items-center gap-4">
                                        <Label htmlFor="apartmentSignature" className="text-right">
                                            Sygnatura Mieszkania
                                        </Label>
                                        <Input
                                            id="apartmentSignature"
                                            className="col-span-3"
                                            {...registerPayment('apartmentSignature')}
                                        />
                                    </div>
                                    {paymentErrors.apartmentSignature &&
                                        <p className="text-red-500 text-sm">{paymentErrors.apartmentSignature.message}</p>}
                                    <div className="grid grid-cols-4 items-center gap-4">
                                        <Label htmlFor="description" className="text-right">
                                            Opis
                                        </Label>
                                        <Textarea
                                            id="description"
                                            className="col-span-3"
                                            {...registerPayment('description')}
                                        />
                                    </div>
                                    {paymentErrors.description &&
                                        <p className="text-red-500 text-sm">{paymentErrors.description.message}</p>}
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
                            <TableHead>Status</TableHead>
                            <TableHead>Data Płatności</TableHead>
                            <TableHead>Kwota</TableHead>
                            <TableHead>Opis</TableHead>
                            <TableHead>Sygnatura Mieszkania</TableHead>
                            <TableHead>Aktywna</TableHead>
                            <TableHead>Akcje</TableHead>
                        </TableRow>
                    </TableHeader>
                    <TableBody>
                        {payments.map((payment) => (
                            <TableRow key={payment.paymentId}>
                                <TableCell>{payment.paymentId}</TableCell>
                                <TableCell>{payment.paymentStatus}</TableCell>
                                <TableCell>{format(new Date(payment.paymentDate), 'dd-MM-yyyy HH:mm:ss')}</TableCell>
                                <TableCell>{payment.paymentAmount}</TableCell>
                                <TableCell>{payment.description}</TableCell>
                                <TableCell>{payment.apartmentSignature}</TableCell>
                                <TableCell>
                                    <Switch
                                        checked={payment.readyToPay}
                                        onCheckedChange={(checked) => handleActivatePayment(payment.paymentId, checked)}
                                    />
                                </TableCell>
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
                                            <DropdownMenuItem onClick={() => {
                                                setEditingPayment(payment)
                                                console.log(payment)
                                                setIsEditPaymentDialogOpen(true)
                                            }}>
                                                <Edit className="mr-2 h-4 w-4"/>
                                                Edytuj Płatność
                                            </DropdownMenuItem>
                                            <DropdownMenuItem onClick={() => {
                                                setEditingPayment(payment)
                                                fetchPaymentComponents(payment.paymentId)
                                                setIsComponentDialogOpen(true)
                                            }}>
                                                <Settings className="mr-2 h-4 w-4"/>
                                                Komponenty
                                            </DropdownMenuItem>
                                            <DropdownMenuSeparator/>
                                            <DropdownMenuItem
                                                onClick={() => handleDeletePayment(payment.paymentId)}
                                                className="text-red-600"
                                            >
                                                <Trash2 className="mr-2 h-4 w-4"/>
                                                Usuń Płatność
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
            <Dialog open={isEditPaymentDialogOpen} onOpenChange={setIsEditPaymentDialogOpen}>
                <DialogContent className="sm:max-w-[425px]">
                    <DialogHeader>
                        <DialogTitle>Edytuj Płatność</DialogTitle>
                        <DialogDescription>
                            Wprowadź nowe dane płatności. Kliknij zapisz, gdy skończysz.
                        </DialogDescription>
                    </DialogHeader>
                    {editingPayment && (
                        <form onSubmit={handleSubmitPayment(handleUpdatePayment)}>
                            <div className="grid gap-4 py-4">
                                <div className="grid grid-cols-4 items-center gap-4">
                                    <Label htmlFor="edit-apartmentSignature" className="text-right">
                                        Sygnatura Mieszkania
                                    </Label>
                                    <Input
                                        id="edit-apartmentSignature"
                                        defaultValue={editingPayment.apartmentSignature}
                                        className="col-span-3"
                                        {...registerPayment('apartmentSignature')}
                                    />
                                </div>
                                {paymentErrors.apartmentSignature &&
                                    <p className="text-red-500 text-sm">{paymentErrors.apartmentSignature.message}</p>}
                                <div className="grid grid-cols-4 items-center gap-4">
                                    <Label htmlFor="edit-description" className="text-right">
                                        Opis
                                    </Label>
                                    <Textarea
                                        id="edit-description"
                                        defaultValue={editingPayment.description}
                                        className="col-span-3"
                                        {...registerPayment('description')}
                                    />
                                </div>
                                {paymentErrors.description &&
                                    <p className="text-red-500 text-sm">{paymentErrors.description.message}</p>}
                            </div>
                            <DialogFooter>
                                <Button type="submit">Zapisz</Button>
                            </DialogFooter>
                        </form>
                    )}
                </DialogContent>
            </Dialog>
            <Dialog open={isComponentDialogOpen} onOpenChange={setIsComponentDialogOpen}>
                <DialogContent className="sm:max-w-[700px]">
                    <DialogHeader>
                        <DialogTitle>Komponenty Płatności</DialogTitle>
                        <DialogDescription>
                            Zarządzaj komponentami dla wybranej płatności.
                        </DialogDescription>
                    </DialogHeader>
                    {editingPayment && (
                        <>
                            <div className="flex justify-end mb-4">
                                <Button onClick={() => {
                                    setIsAddingComponent(true)
                                    setEditingComponent(null)
                                    resetComponent()
                                }}>
                                    <Plus className="mr-2 h-4 w-4"/> Dodaj Komponent
                                </Button>
                            </div>
                            {(isAddingComponent || editingComponent) && (
                                <form
                                    onSubmit={handleSubmitComponent(editingComponent ? handleUpdateComponent : handleAddComponent)}>
                                    <div className="grid gap-4 py-4">
                                        <div className="grid grid-cols-4 items-center gap-4">
                                            <Label htmlFor="componentType" className="text-right">
                                                Typ Komponentu
                                            </Label>
                                            <Input
                                                id="componentType"
                                                className="col-span-3"
                                                {...registerComponent('componentType')}
                                                defaultValue={editingComponent?.componentType}
                                            />
                                        </div>
                                        {componentErrors.componentType &&
                                            <p className="text-red-500 text-sm">{componentErrors.componentType.message}</p>}
                                        <div className="grid grid-cols-4 items-center gap-4">
                                            <Label htmlFor="unitPrice" className="text-right">
                                                Cena Jednostkowa
                                            </Label>
                                            <Input
                                                id="unitPrice"
                                                type="number"
                                                step="0.01"
                                                className="col-span-3"
                                                {...registerComponent('unitPrice', {valueAsNumber: true})}
                                                defaultValue={editingComponent?.unitPrice}
                                            />
                                        </div>
                                        {componentErrors.unitPrice &&
                                            <p className="text-red-500 text-sm">{componentErrors.unitPrice.message}</p>}
                                        <div className="grid grid-cols-4 items-center gap-4">
                                            <Label htmlFor="specialMultiplier" className="text-right">
                                                Mnożnik Specjalny
                                            </Label>
                                            <Input
                                                id="specialMultiplier"
                                                type="number"
                                                step="0.01"
                                                className="col-span-3"
                                                {...registerComponent('specialMultiplier', {valueAsNumber: true})}
                                                defaultValue={editingComponent?.specialMultiplier}
                                            />
                                        </div>
                                        {componentErrors.specialMultiplier &&
                                            <p className="text-red-500 text-sm">{componentErrors.specialMultiplier.message}</p>}
                                        <div className="grid grid-cols-4 items-center gap-4">
                                            <Label htmlFor="componentAmount" className="text-right">
                                                Ilość
                                            </Label>
                                            <Input
                                                id="componentAmount"
                                                type="number"
                                                step="0.01"
                                                className="col-span-3"
                                                {...registerComponent('componentAmount', {valueAsNumber: true})}
                                                defaultValue={editingComponent?.componentAmount}
                                            />
                                        </div>
                                        {componentErrors.componentAmount &&
                                            <p className="text-red-500 text-sm">{componentErrors.componentAmount.message}</p>}
                                        <div className="grid grid-cols-4 items-center gap-4">
                                            <Label htmlFor="unit" className="text-right">
                                                Jednostka
                                            </Label>
                                            <Input
                                                id="unit"
                                                className="col-span-3"
                                                {...registerComponent('unit')}
                                                defaultValue={editingComponent?.unit}
                                            />
                                        </div>
                                        {componentErrors.unit &&
                                            <p className="text-red-500 text-sm">{componentErrors.unit.message}</p>}
                                    </div>
                                    <DialogFooter>
                                        <Button
                                            type="submit">{editingComponent ? 'Aktualizuj' : 'Dodaj'} Komponent</Button>
                                        <Button type="button" variant="outline" onClick={() => {
                                            setIsAddingComponent(false)
                                            setEditingComponent(null)
                                            resetComponent()
                                        }}>Anuluj</Button>
                                    </DialogFooter>
                                </form>
                            )}
                            <Table>
                                <TableHeader>
                                    <TableRow>
                                        <TableHead>Typ</TableHead>
                                        <TableHead>Cena Jednostkowa</TableHead>
                                        <TableHead>Mnożnik</TableHead>
                                        <TableHead>Ilość</TableHead>
                                        <TableHead>Jednostka</TableHead>
                                        <TableHead>Akcje</TableHead>
                                    </TableRow>
                                </TableHeader>
                                <TableBody>
                                    {paymentComponents.map((component) => (
                                        <TableRow key={component.id}>
                                            <TableCell>{component.componentType}</TableCell>
                                            <TableCell>{component.unitPrice}</TableCell>
                                            <TableCell>{component.specialMultiplier}</TableCell>
                                            <TableCell>{component.componentAmount}</TableCell>
                                            <TableCell>{component.unit}</TableCell>
                                            <TableCell>
                                                <Button variant="ghost" size="sm" onClick={() => {
                                                    setEditingComponent(component)
                                                    setIsAddingComponent(false)
                                                }}>
                                                    <Edit className="h-4 w-4"/>
                                                </Button>
                                                <Button variant="ghost" size="sm"
                                                        onClick={() => handleDeleteComponent(component.id)}>
                                                    <Trash2 className="h-4 w-4"/>
                                                </Button>
                                            </TableCell>
                                        </TableRow>
                                    ))}
                                </TableBody>
                            </Table>
                        </>
                    )}
                </DialogContent>
            </Dialog>
        </Card>
    )
}