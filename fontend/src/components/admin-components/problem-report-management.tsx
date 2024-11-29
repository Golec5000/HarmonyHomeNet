'use client'

import React, {useEffect, useState} from 'react'
import {format} from 'date-fns'
import {toast} from 'sonner'
import {z} from 'zod'
import {Controller, useForm} from 'react-hook-form'
import {zodResolver} from '@hookform/resolvers/zod'
import {Table, TableBody, TableCell, TableHead, TableHeader, TableRow,} from "@/components/ui/table"
import {
    Dialog,
    DialogContent,
    DialogDescription,
    DialogFooter,
    DialogHeader,
    DialogTitle,
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
import {Label} from "@/components/ui/label"
import {Textarea} from "@/components/ui/textarea"
import {Select, SelectContent, SelectItem, SelectTrigger, SelectValue,} from "@/components/ui/select"
import {Card, CardContent, CardHeader, CardTitle,} from "@/components/ui/card"
import {Collapsible, CollapsibleContent, CollapsibleTrigger,} from "@/components/ui/collapsible"
import {
    AlertTriangle,
    Calendar,
    CheckCircle,
    ChevronDown,
    ChevronLeft,
    ChevronRight,
    ChevronUp,
    Clock,
    DollarSign,
    DrillIcon,
    Edit,
    FileText,
    HelpCircle,
    Home,
    MoreHorizontal,
    Trash2,
    User,
    XCircle,
} from 'lucide-react'

interface ProblemReport {
    id: number
    note: string
    reportStatus: 'OPEN' | 'IN_PROGRESS' | 'DONE'
    category: 'GENERAL' | 'TECHNICAL' | 'FINANCIAL' | 'OTHER'
    userName: string
    apartmentAddress: string
    endDate: string | null
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

const problemReportSchema = z.object({
    note: z.string().max(1000),
    reportStatus: z.enum(['OPEN', 'IN_PROGRESS', 'DONE']),
    category: z.enum(['GENERAL', 'TECHNICAL', 'FINANCIAL', 'OTHER']),
})

export function ProblemReportManagementComponent() {
    const [problemReports, setProblemReports] = useState<ProblemReport[]>([])
    const [currentPage, setCurrentPage] = useState(0)
    const [totalPages, setTotalPages] = useState(0)
    const [isEditDialogOpen, setIsEditDialogOpen] = useState(false)
    const [editingReport, setEditingReport] = useState<ProblemReport | null>(null)
    const [expandedReportId, setExpandedReportId] = useState<number | null>(null)

    const {control, handleSubmit, reset, formState: {errors}} = useForm<z.infer<typeof problemReportSchema>>({
        resolver: zodResolver(problemReportSchema),
    })

    useEffect(() => {
        fetchProblemReports()
    }, [currentPage])

    const fetchProblemReports = async () => {
        try {
            const response = await fetch(`http://localhost:8444/bwp/hhn/api/v1/problem-report/get-all-reports?pageNo=${currentPage}&pageSize=10`, {
                headers: {
                    'Authorization': `Bearer ${sessionStorage.getItem('jwt_accessToken')}`
                }
            })
            if (response.ok) {
                const data: PageResponse<ProblemReport> = await response.json()
                setProblemReports(data.content)
                setTotalPages(data.totalPages)
            } else {
                toast.error('Failed to fetch problem reports')
            }
        } catch (error) {
            console.error('Error fetching problem reports:', error)
            toast.error('An error occurred while fetching problem reports')
        }
    }

    const handleUpdateReport = async (data: z.infer<typeof problemReportSchema>) => {
        if (editingReport) {
            try {
                const response = await fetch(`http://localhost:8444/bwp/hhn/api/v1/problem-report/update-report/${editingReport.id}`, {
                    method: 'PUT',
                    headers: {
                        'Content-Type': 'application/json',
                        'Authorization': `Bearer ${sessionStorage.getItem('jwt_accessToken')}`
                    },
                    body: JSON.stringify(data)
                })
                if (response.ok) {
                    toast.success('Problem report updated successfully')
                    setIsEditDialogOpen(false)
                    setEditingReport(null)
                    reset()
                    fetchProblemReports()
                } else {
                    toast.error('Failed to update problem report')
                }
            } catch (error) {
                console.error('Error updating problem report:', error)
                toast.error('An error occurred while updating the problem report')
            }
        }
    }

    const handleDeleteReport = async (id: number) => {
        try {
            const response = await fetch(`http://localhost:8444/bwp/hhn/api/v1/problem-report/delete-report/${id}`, {
                method: 'DELETE',
                headers: {
                    'Authorization': `Bearer ${sessionStorage.getItem('jwt_accessToken')}`
                }
            })
            if (response.ok) {
                toast.success('Problem report deleted successfully')
                fetchProblemReports()
            } else {
                toast.error('Failed to delete problem report')
            }
        } catch (error) {
            console.error('Error deleting problem report:', error)
            toast.error('An error occurred while deleting the problem report')
        }
    }

    const toggleReportExpansion = (id: number) => {
        setExpandedReportId(expandedReportId === id ? null : id)
    }

    const getStatusIcon = (status: string) => {
        switch (status) {
            case 'OPEN':
                return <AlertTriangle className="h-4 w-4 text-yellow-500"/>
            case 'IN_PROGRESS':
                return <Clock className="h-4 w-4 text-blue-500"/>
            case 'DONE':
                return <CheckCircle className="h-4 w-4 text-green-500"/>
            default:
                return <XCircle className="h-4 w-4 text-red-500"/>
        }
    }

    const getCategoryIcon = (category: string) => {
        switch (category) {
            case 'GENERAL':
                return <FileText className="h-4 w-4 text-gray-500"/>
            case 'TECHNICAL':
                return <DrillIcon className="h-4 w-4 text-blue-500"/>
            case 'FINANCIAL':
                return <DollarSign className="h-4 w-4 text-green-500"/>
            case 'OTHER':
                return <HelpCircle className="h-4 w-4 text-purple-500"/>
            default:
                return <HelpCircle className="h-4 w-4 text-gray-500"/>
        }
    }

    return (
        <Card>
            <CardHeader>
                <CardTitle className="text-2xl font-bold flex items-center space-x-2">
                    <AlertTriangle className="h-6 w-6 text-primary"/>
                    <span>Zarządzanie Zgłoszeniami Problemów</span>
                </CardTitle>
            </CardHeader>
            <CardContent>
                <Table>
                    <TableHeader>
                        <TableRow>
                            <TableHead>ID</TableHead>
                            <TableHead>Notatka</TableHead>
                            <TableHead>Status</TableHead>
                            <TableHead>Kategoria</TableHead>
                            <TableHead>Użytkownik</TableHead>
                            <TableHead>Adres Mieszkania</TableHead>
                            <TableHead>Data Zakończenia</TableHead>
                            <TableHead>Akcje</TableHead>
                        </TableRow>
                    </TableHeader>
                    <TableBody>
                        {problemReports.map((report) => (
                            <TableRow key={report.id}>
                                <TableCell>{report.id}</TableCell>
                                <TableCell>
                                    <Collapsible>
                                        <CollapsibleTrigger asChild>
                                            <Button variant="ghost" size="sm" className="p-0">
                                                {expandedReportId === report.id ? (
                                                    <ChevronUp className="h-4 w-4 mr-2"/>
                                                ) : (
                                                    <ChevronDown className="h-4 w-4 mr-2"/>
                                                )}
                                                <FileText className="h-4 w-4 mr-2"/>
                                                {report.note.substring(0, 50)}...
                                            </Button>
                                        </CollapsibleTrigger>
                                        <CollapsibleContent className="mt-2">
                                            {report.note}
                                        </CollapsibleContent>
                                    </Collapsible>
                                </TableCell>
                                <TableCell>
                                    <div className="flex items-center">
                                        {getStatusIcon(report.reportStatus)}
                                        <span className="ml-2">{report.reportStatus}</span>
                                    </div>
                                </TableCell>
                                <TableCell>
                                    <div className="flex items-center">
                                        {getCategoryIcon(report.category)}
                                        <span className="ml-2">{report.category}</span>
                                    </div>
                                </TableCell>
                                <TableCell>
                                    <div className="flex items-center">
                                        <User className="h-4 w-4 mr-2"/>
                                        {report.userName}
                                    </div>
                                </TableCell>
                                <TableCell>
                                    <div className="flex items-center">
                                        <Home className="h-4 w-4 mr-2"/>
                                        {report.apartmentAddress}
                                    </div>
                                </TableCell>
                                <TableCell>
                                    <div className="flex items-center">
                                        <Calendar className="h-4 w-4 mr-2"/>
                                        {report.endDate ? format(new Date(report.endDate), 'dd-MM-yyyy HH:mm:ss') : 'N/A'}
                                    </div>
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
                                                setEditingReport(report)
                                                setIsEditDialogOpen(true)
                                            }}>
                                                <Edit className="h-4 w-4 mr-2"/>
                                                Edytuj Zgłoszenie
                                            </DropdownMenuItem>
                                            <DropdownMenuSeparator/>
                                            <DropdownMenuItem
                                                onClick={() => handleDeleteReport(report.id)}
                                                className="text-red-600"
                                            >
                                                <Trash2 className="h-4 w-4 mr-2"/>
                                                Usuń Zgłoszenie
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
            <Dialog open={isEditDialogOpen} onOpenChange={setIsEditDialogOpen}>
                <DialogContent className="sm:max-w-[425px]">
                    <DialogHeader>
                        <DialogTitle>Edytuj Zgłoszenie Problemu</DialogTitle>
                        <DialogDescription>
                            Wprowadź nowe dane zgłoszenia. Kliknij zapisz, gdy skończysz.
                        </DialogDescription>
                    </DialogHeader>
                    {editingReport && (
                        <form onSubmit={handleSubmit(handleUpdateReport)}>
                            <div className="grid gap-4 py-4">
                                <div className="grid grid-cols-4 items-center gap-4">
                                    <Label htmlFor="note" className="text-right">
                                        Notatka
                                    </Label>
                                    <Controller
                                        name="note"
                                        control={control}
                                        defaultValue={editingReport.note}
                                        render={({field}) => (
                                            <Textarea
                                                id="note"
                                                className="col-span-3"
                                                {...field}
                                            />
                                        )}
                                    />
                                </div>
                                {errors.note && <p className="text-red-500 text-sm">{errors.note.message}</p>}
                                <div className="grid grid-cols-4 items-center gap-4">
                                    <Label htmlFor="reportStatus" className="text-right">
                                        Status
                                    </Label>
                                    <Controller
                                        name="reportStatus"
                                        control={control}
                                        defaultValue={editingReport.reportStatus}
                                        render={({field}) => (
                                            <Select onValueChange={field.onChange} defaultValue={field.value}>
                                                <SelectTrigger className="col-span-3">
                                                    <SelectValue placeholder="Wybierz status"/>
                                                </SelectTrigger>
                                                <SelectContent>
                                                    <SelectItem value="OPEN">OPEN</SelectItem>
                                                    <SelectItem value="IN_PROGRESS">IN_PROGRESS</SelectItem>
                                                    <SelectItem value="DONE">DONE</SelectItem>
                                                </SelectContent>
                                            </Select>
                                        )}
                                    />
                                </div>
                                <div className="grid grid-cols-4 items-center gap-4">
                                    <Label htmlFor="category" className="text-right">
                                        Kategoria
                                    </Label>
                                    <Controller
                                        name="category"
                                        control={control}
                                        defaultValue={editingReport.category}
                                        render={({field}) => (
                                            <Select onValueChange={field.onChange} defaultValue={field.value}>
                                                <SelectTrigger className="col-span-3">
                                                    <SelectValue placeholder="Wybierz kategorię"/>
                                                </SelectTrigger>
                                                <SelectContent>
                                                    <SelectItem value="GENERAL">GENERAL</SelectItem>
                                                    <SelectItem value="TECHNICAL">TECHNICAL</SelectItem>
                                                    <SelectItem value="FINANCIAL">FINANCIAL</SelectItem>
                                                    <SelectItem value="OTHER">OTHER</SelectItem>
                                                </SelectContent>
                                            </Select>
                                        )}
                                    />
                                </div>
                            </div>
                            <DialogFooter>
                                <Button type="submit">Zapisz</Button>
                            </DialogFooter>
                        </form>
                    )}
                </DialogContent>
            </Dialog>
        </Card>
    )
}