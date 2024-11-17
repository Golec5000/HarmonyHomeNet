'use client'

import React, {useEffect, useState} from 'react'
import {Card, CardContent, CardHeader, CardTitle} from "@/components/ui/card"
import {Button} from "@/components/ui/button"
import {Label} from "@/components/ui/label"
import {Textarea} from "@/components/ui/textarea"
import {Select, SelectContent, SelectItem, SelectTrigger, SelectValue} from "@/components/ui/select"
import {Table, TableBody, TableCell, TableHead, TableHeader, TableRow} from "@/components/ui/table"
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
    FileText,
    HelpCircle,
    Mail,
    MessageSquare,
    Phone,
    Send
} from 'lucide-react'
import {toast} from "sonner"
import {jwtDecode} from "jwt-decode"
import {format} from 'date-fns'
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
    const [message, setMessage] = useState('')
    const [category, setCategory] = useState('')
    const [categories, setCategories] = useState<Category[]>([])
    const [isLoading, setIsLoading] = useState(false)
    const [reports, setReports] = useState<Report[]>([])
    const [currentPage, setCurrentPage] = useState(0)
    const [totalPages, setTotalPages] = useState(0)
    const [expandedReportId, setExpandedReportId] = useState<string | null>(null)

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

    const getStatusIcon = (status: string) => {
        switch (status) {
            case 'OPEN':
                return <AlertTriangle className="h-4 w-4 text-yellow-500"/>
            case 'IN_PROGRESS':
                return <Clock className="h-4 w-4 text-blue-500"/>
            case 'DONE':
                return <CheckCircle className="h-4 w-4 text-green-500"/>
            default:
                return null
        }
    }

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
                            <CardTitle className="flex items-center">
                                <Send className="mr-2 h-6 w-6"/>
                                Wyślij Wiadomość
                            </CardTitle>
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
                        <Table>
                            <TableHeader>
                                <TableRow>
                                    <TableHead>ID Zgłoszenia</TableHead>
                                    <TableHead>Kategoria</TableHead>
                                    <TableHead>Status</TableHead>
                                    <TableHead>Data Zakończenia</TableHead>
                                    <TableHead>Akcje</TableHead>
                                </TableRow>
                            </TableHeader>
                            <TableBody>
                                {reports.map((report) => (
                                    <React.Fragment key={report.id}>
                                        <TableRow>
                                            <TableCell>{report.id}</TableCell>
                                            <TableCell>
                                                <div className="flex items-center">
                                                    {getCategoryIcon(report.category)}
                                                    <span className="ml-2">{report.category}</span>
                                                </div>
                                            </TableCell>
                                            <TableCell>
                                                <div className="flex items-center">
                                                    {getStatusIcon(report.reportStatus)}
                                                    <span className="ml-2">{report.reportStatus}</span>
                                                </div>
                                            </TableCell>
                                            <TableCell>
                                                <div className="flex items-center">
                                                    <Calendar className="h-4 w-4 mr-2"/>
                                                    {report.endDate ? format(new Date(report.endDate), 'dd-MM-yyyy HH:mm:ss') : 'N/A'}
                                                </div>
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
                </Card>
            </div>
        </div>
    )
}