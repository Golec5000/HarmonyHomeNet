'use client'

import React, {useEffect, useState} from 'react'
import {Card, CardContent, CardHeader, CardTitle} from "@/components/ui/card"
import {Button} from "@/components/ui/button"
import {Input} from "@/components/ui/input"
import {Label} from "@/components/ui/label"
import {Textarea} from "@/components/ui/textarea"
import {Select, SelectContent, SelectItem, SelectTrigger, SelectValue} from "@/components/ui/select"
import {Table, TableBody, TableCell, TableHead, TableHeader, TableRow} from "@/components/ui/table"
import {
    ChevronDown,
    ChevronLeft,
    ChevronRight,
    ChevronUp,
    Clock,
    Mail,
    MessageSquare,
    Phone,
    Search
} from 'lucide-react'
import {toast} from "sonner"
import {jwtDecode} from "jwt-decode"
import {format, parseISO} from 'date-fns'
import {Collapsible, CollapsibleContent} from "@/components/ui/collapsible"

type Category = {
    id: string
    value: string
    label: string
}

type Report = {
    id: string
    category: string
    reportStatus: string
    endDate: string
    note: string
}

interface ContactAdminProps {
    apartmentSignature: string | null;
}

export function ContactAdmin({apartmentSignature}: ContactAdminProps) {
    const [subject, setSubject] = useState('')
    const [message, setMessage] = useState('')
    const [category, setCategory] = useState('')
    const [categories, setCategories] = useState<Category[]>([])
    const [isLoading, setIsLoading] = useState(false)
    const [reports, setReports] = useState<Report[]>([])
    const [currentPage, setCurrentPage] = useState(0)
    const [totalPages, setTotalPages] = useState(0)
    const [expandedReportId, setExpandedReportId] = useState<string | null>(null)
    const [searchTerm, setSearchTerm] = useState('')
    const [sortColumn, setSortColumn] = useState<keyof Report>('id')
    const [sortDirection, setSortDirection] = useState<'asc' | 'desc'>('asc')
    const [filterStatus, setFilterStatus] = useState('All')

    useEffect(() => {
        const fetchCategories = async () => {
            try {
                const response = await fetch(`http://localhost:8444/bwp/hhn/api/v1/problem-report/get-types-of-reports`, {
                    headers: {
                        'Authorization': `Bearer ${localStorage.getItem('jwt_accessToken')}`
                    },
                });
                if (response.ok) {
                    const data = await response.json()
                    const formattedCategories = data.map((cat: string, index: number) => ({
                        id: index.toString(),
                        value: cat,
                        label: cat.charAt(0) + cat.slice(1).toLowerCase()
                    }));
                    setCategories(formattedCategories)
                } else if (response.status === 401 || response.status === 403) {
                    window.location.href = '/login';
                } else {
                    console.error('Failed to fetch categories:', response.statusText);
                }
            } catch (error) {
                console.error('Error fetching categories:', error)
                toast.error("Failed to load categories. Please try again later.")
            }
        }

        fetchCategories()
    }, [])

    useEffect(() => {
        const fetchReports = async (page: number) => {
            if (!apartmentSignature) return;

            try {
                const response = await fetch(`http://localhost:8444/bwp/hhn/api/v1/problem-report/get-report-by-apartment/${apartmentSignature}?pageNo=${page}&pageSize=5`, {
                    headers: {
                        'Authorization': `Bearer ${localStorage.getItem('jwt_accessToken')}`
                    },
                });
                if (response.ok) {
                    const data = await response.json()
                    setReports(data.content)
                    setTotalPages(data.totalPages)
                } else if (response.status === 401 || response.status === 403) {
                    window.location.href = '/login';
                } else {
                    console.error('Failed to fetch reports:', response.statusText);
                }
            } catch (error) {
                console.error('Error fetching reports:', error)
                toast.error("Failed to load reports. Please try again later.")
            }
        }

        fetchReports(currentPage)
    }, [apartmentSignature, currentPage])

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault()
        setIsLoading(true)
        try {
            const token = localStorage.getItem('jwt_accessToken')
            const decodedToken = token ? jwtDecode<{ userId: string }>(token) : null
            const userId = decodedToken ? decodedToken.userId : null

            const report = JSON.stringify({
                reportStatus: "OPEN",
                category,
                userId,
                apartmentSignature,
                note: message
            });

            console.log('Report:', report)

            const response = await fetch('http://localhost:8444/bwp/hhn/api/v1/problem-report/create-report', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${token}`
                },
                body: report,
            })

            if (response.ok) {
                setSubject('')
                setMessage('')
                setCategory('')
                toast.success('Message sent to administrators!')
            } else if (response.status === 401 || response.status === 403) {
                window.location.href = '/login';
            } else {
                console.error('Failed to send message:', response.statusText);
            }

            // Refresh the reports list
            const reportsResponse = await fetch(`http://localhost:8444/bwp/hhn/api/v1/problem-report/get-report-by-apartment/${apartmentSignature}?pageNo=${currentPage}&pageSize=5`, {
                headers: {
                    'Authorization': `Bearer ${token}`
                },
            });

            if (reportsResponse.ok) {
                const data = await reportsResponse.json()
                setReports(data.content)
                setTotalPages(data.totalPages)
            } else if (response.status === 401 || response.status === 403) {
                window.location.href = '/login';
            } else {
                console.error('Failed to fetch reports:', response.statusText);
            }

        } catch (error) {
            console.error('Error sending message:', error)
            toast.error('Failed to send message. Please try again.')
        } finally {
            setIsLoading(false)
        }
    }

    const handlePrevious = () => {
        if (currentPage > 0) {
            setCurrentPage((prev) => prev - 1)
        }
    }

    const handleNext = () => {
        if (currentPage < totalPages - 1) {
            setCurrentPage((prev) => prev + 1)
        }
    }

    const toggleReportExpansion = (reportId: string) => {
        setExpandedReportId(expandedReportId === reportId ? null : reportId)
    }

    const handleSort = (column: keyof Report) => {
        if (column === sortColumn) {
            setSortDirection(sortDirection === 'asc' ? 'desc' : 'asc')
        } else {
            setSortColumn(column)
            setSortDirection('asc')
        }
    }

    const filteredAndSortedReports = reports
        .filter(report =>
            (report.id.toString().toLowerCase().includes(searchTerm.toLowerCase()) ||
                report.category.toLowerCase().includes(searchTerm.toLowerCase())) &&
            (filterStatus === 'All' || report.reportStatus === filterStatus)
        )
        .sort((a, b) => {
            if (a[sortColumn] < b[sortColumn]) return sortDirection === 'asc' ? -1 : 1
            if (a[sortColumn] > b[sortColumn]) return sortDirection === 'asc' ? 1 : -1
            return 0
        })

    return (
        <div className="space-y-6">
            <h1 className="text-3xl font-bold flex items-center">
                <MessageSquare className="mr-2 h-8 w-8 text-primary"/>
                Kontakt z Administracją
            </h1>

            <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                <div className="space-y-6">
                    <Card>
                        <CardHeader>
                            <CardTitle>Wyślij Wiadomość</CardTitle>
                        </CardHeader>
                        <CardContent>
                            <form onSubmit={handleSubmit} className="space-y-4">
                                <div className="space-y-2">
                                    <Label htmlFor="category">Kategoria</Label>
                                    <Select value={category} onValueChange={setCategory}>
                                        <SelectTrigger id="category">
                                            <SelectValue placeholder="Wybierz kategorię"/>
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
                                    <Label htmlFor="message">Wiadomość</Label>
                                    <Textarea
                                        id="message"
                                        value={message}
                                        onChange={(e) => setMessage(e.target.value)}
                                        placeholder="Podaj więcej szczegółów dotyczących problemu lub zapytania"
                                        required
                                        className="min-h-[150px]"
                                    />
                                </div>
                                <Button type="submit" className="w-full" disabled={isLoading}>
                                    {isLoading ? 'Wysyłanie...' : 'Wyślij Wiadomość'}
                                </Button>
                            </form>
                        </CardContent>
                    </Card>

                    <Card>
                        <CardHeader>
                            <CardTitle>Informacje Kontaktowe</CardTitle>
                        </CardHeader>
                        <CardContent className="space-y-4">
                            <div className="flex items-center space-x-2">
                                <Phone className="h-5 w-5 text-primary"/>
                                <span>Alarmowy: (555) 123-4567</span>
                            </div>
                            <div className="flex items-center space-x-2">
                                <Mail className="h-5 w-5 text-primary"/>
                                <span>Email: admin@ebok.com</span>
                            </div>
                            <div className="flex items-center space-x-2">
                                <Clock className="h-5 w-5 text-primary"/>
                                <span>Godziny pracy: Pon-Pt, 9:00-17:00</span>
                            </div>
                            <Card>
                                <CardHeader>
                                    <CardTitle className="text-sm">Uwaga</CardTitle>
                                </CardHeader>
                                <CardContent>
                                    <p className="text-sm text-muted-foreground">
                                        W nagłych wypadkach poza godzinami pracy prosimy o kontakt pod numerem
                                        alarmowym.
                                        W sprawach mniej pilnych odpowiemy na wiadomość w ciągu 1-2 dni roboczych.
                                    </p>
                                </CardContent>
                            </Card>
                        </CardContent>
                    </Card>
                </div>

                <Card>
                    <CardHeader>
                        <CardTitle>Twoje Zgłoszenia</CardTitle>
                    </CardHeader>
                    <CardContent>
                        <div className="flex justify-between items-center mb-4">
                            <div className="flex items-center space-x-2">
                                <Search className="h-5 w-5 text-muted-foreground"/>
                                <Input
                                    placeholder="Szukaj zgłoszeń..."
                                    value={searchTerm}
                                    onChange={(e) => setSearchTerm(e.target.value)}
                                    className="w-64"
                                />
                            </div>
                            <Select value={filterStatus} onValueChange={setFilterStatus}>
                                <SelectTrigger className="w-[180px]">
                                    <SelectValue placeholder="Filtruj po statusie"/>
                                </SelectTrigger>
                                <SelectContent>
                                    <SelectItem value="All">Wszystkie</SelectItem>
                                    <SelectItem value="OPEN">Otwarte</SelectItem>
                                    <SelectItem value="DONE">Zamknięte</SelectItem>
                                    <SelectItem value="IN_PROGRESS">W trakcie</SelectItem>
                                </SelectContent>
                            </Select>
                        </div>
                        <Table>
                            <TableHeader>
                                <TableRow>
                                    <TableHead onClick={() => handleSort('id')} className="cursor-pointer">
                                        ID Zgłoszenia {sortColumn === 'id' && (sortDirection === 'asc' ? '▲' : '▼')}
                                    </TableHead>
                                    <TableHead onClick={() => handleSort('category')} className="cursor-pointer">
                                        Kategoria {sortColumn === 'category' && (sortDirection === 'asc' ? '▲' : '▼')}
                                    </TableHead>
                                    <TableHead onClick={() => handleSort('reportStatus')} className="cursor-pointer">
                                        Status {sortColumn === 'reportStatus' && (sortDirection === 'asc' ? '▲' : '▼')}
                                    </TableHead>
                                    <TableHead onClick={() => handleSort('endDate')} className="cursor-pointer">
                                        Data
                                        Zakończenia {sortColumn === 'endDate' && (sortDirection === 'asc' ? '▲' : '▼')}
                                    </TableHead>
                                    <TableHead>Akcje</TableHead>
                                </TableRow>
                            </TableHeader>
                            <TableBody>
                                {filteredAndSortedReports.map((report) => (
                                    <React.Fragment key={report.id}>
                                        <TableRow>
                                            <TableCell>{report.id}</TableCell>
                                            <TableCell>{report.category}</TableCell>
                                            <TableCell>{report.reportStatus}</TableCell>
                                            <TableCell>
                                                {report.endDate ? format(parseISO(report.endDate), 'd MMMM, yyyy') : 'Brak'}
                                            </TableCell>
                                            <TableCell>
                                                <Button
                                                    variant="ghost"
                                                    size="sm"
                                                    onClick={() => toggleReportExpansion(report.id)}
                                                >
                                                    {expandedReportId === report.id ? (
                                                        <ChevronUp className="h-4 w-4"/>
                                                    ) : (
                                                        <ChevronDown className="h-4 w-4"/>
                                                    )}
                                                </Button>
                                            </TableCell>
                                        </TableRow>
                                        <TableRow>
                                            <TableCell colSpan={5}>
                                                <Collapsible open={expandedReportId === report.id}>
                                                    <CollapsibleContent>
                                                        <Card className="mt-2">
                                                            <CardContent className="pt-2">
                                                                <p className="text-sm font-medium">Notatka:</p>
                                                                <p className="text-sm">{report.note}</p>
                                                            </CardContent>
                                                        </Card>
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
                                onClick={handlePrevious}
                                disabled={currentPage === 0}
                            >
                                <ChevronLeft className="h-4 w-4"/>
                                Poprzednia
                            </Button>
                            <span className="text-sm">
                                Strona {currentPage + 1} z {totalPages}
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
                    </CardContent>
                </Card>
            </div>
        </div>
    )
}