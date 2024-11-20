'use client'

import React, {useEffect, useState} from 'react'
import {Card, CardContent, CardHeader, CardTitle} from "@/components/ui/card"
import {Button} from "@/components/ui/button"
import {Input} from "@/components/ui/input"
import {Table, TableBody, TableCell, TableHead, TableHeader, TableRow} from "@/components/ui/table"
import {ChevronLeft, ChevronRight, Download, FileText, Search} from 'lucide-react'
import {jwtDecode} from "jwt-decode"
import {toast} from "sonner"
import {format, parseISO} from "date-fns"
import {saveAs} from 'file-saver'
import {Select, SelectContent, SelectItem, SelectTrigger, SelectValue} from "@/components/ui/select"

interface Document {
    documentId: string
    documentName: string
    documentType: string
    documentExtension: string
    createdAt: string
}

interface PageResponse {
    content: Document[]
    totalPages: number
    totalElements: number
    size: number
    number: number
    hasNext: boolean
    hasPrev: boolean
}

export function DocumentsSection() {
    const [documents, setDocuments] = useState<Document[]>([])
    const [documentIds, setDocumentIds] = useState<string[]>([])
    const [currentPage, setCurrentPage] = useState(0)
    const [totalPages, setTotalPages] = useState(0)
    const [isLoading, setIsLoading] = useState(true)
    const [error, setError] = useState<string | null>(null)
    const [searchTerm, setSearchTerm] = useState('')
    const [sortColumn, setSortColumn] = useState<keyof Document>('documentName')
    const [sortDirection, setSortDirection] = useState<'asc' | 'desc'>('asc')
    const [filterType, setFilterType] = useState('All')

    const fetchDocuments = async (page: number) => {
        try {
            const token = sessionStorage.getItem('jwt_accessToken')
            const userId = token ? (jwtDecode<{ userId: string }>(token)).userId : null
            const response = await fetch(`http://localhost:8444/bwp/hhn/api/v1/document/get-all-documents-by-user-id?userId=${userId}&pageNo=${page}&pageSize=5`, {
                headers: {
                    'Authorization': `Bearer ${token}`,
                },
            })

            if (response.ok) {
                const data: PageResponse = await response.json()
                setDocuments(data.content)
                setDocumentIds(data.content.map(doc => doc.documentId))
                setTotalPages(data.totalPages)
            } else if (response.status === 401 || response.status === 403) {
                window.location.href = '/login'
            } else {
                console.error('Failed to fetch documents:', response.statusText)
            }
        } catch (err) {
            setError('Error fetching documents. Please try again later.')
        } finally {
            setIsLoading(false)
        }
    }

    useEffect(() => {
        fetchDocuments(currentPage)
    }, [currentPage])

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

    const handleDownload = async (documentId: string, documentName: string, documentExtension: string) => {
        try {
            const token = sessionStorage.getItem('jwt_accessToken')
            const response = await fetch(`http://localhost:8444/bwp/hhn/api/v1/document/download-document?documentId=${documentId}`, {
                headers: {
                    'Authorization': `Bearer ${token}`
                }
            })

            if (response.ok) {
                const blob = await response.blob()
                saveAs(blob, documentName + '.' + documentExtension)
                toast.success('Document downloaded successfully')
            } else if (response.status === 401 || response.status === 403) {
                window.location.href = '/login'
            } else {
                console.error('Failed to fetch documents:', response.statusText)
            }
        } catch (error) {
            console.error('Error downloading document:', error)
            toast.error('Failed to download document. Please try again.')
        }
    }

    const handleSort = (column: keyof Document) => {
        if (column === sortColumn) {
            setSortDirection(sortDirection === 'asc' ? 'desc' : 'asc')
        } else {
            setSortColumn(column)
            setSortDirection('asc')
        }
    }

    const filteredAndSortedDocuments = documents
        .filter(doc =>
            (doc.documentName.toLowerCase().includes(searchTerm.toLowerCase()) ||
                doc.documentType.toLowerCase().includes(searchTerm.toLowerCase())) &&
            (filterType === 'All' || doc.documentType === filterType)
        )
        .sort((a, b) => {
            if (a[sortColumn] < b[sortColumn]) return sortDirection === 'asc' ? -1 : 1
            if (a[sortColumn] > b[sortColumn]) return sortDirection === 'asc' ? 1 : -1
            return 0
        })

    return (
        <div className="space-y-6">
            <h1 className="text-3xl font-bold flex items-center">
                <FileText className="mr-2 h-8 w-8 text-primary"/>
                Documents
            </h1>

            <Card>
                <CardHeader>
                    <CardTitle>Your Documents</CardTitle>
                </CardHeader>
                <CardContent>
                    {isLoading ? (
                        <div className="text-center">Loading documents...</div>
                    ) : error ? (
                        <div className="text-center text-destructive">{error}</div>
                    ) : (
                        <>
                            <div className="flex justify-between items-center mb-4">
                                <div className="flex items-center space-x-2">
                                    <Search className="h-5 w-5 text-muted-foreground"/>
                                    <Input
                                        placeholder="Search documents..."
                                        value={searchTerm}
                                        onChange={(e) => setSearchTerm(e.target.value)}
                                        className="w-64"
                                    />
                                </div>
                                <Select value={filterType} onValueChange={setFilterType}>
                                    <SelectTrigger className="w-[180px]">
                                        <SelectValue placeholder="Filter by type"/>
                                    </SelectTrigger>
                                    <SelectContent>
                                        <SelectItem value="All">All Types</SelectItem>
                                        {Array.from(new Set(documents.map(doc => doc.documentType))).map(type => (
                                            <SelectItem key={type} value={type}>{type}</SelectItem>
                                        ))}
                                    </SelectContent>
                                </Select>
                            </div>
                            <Table>
                                <TableHeader>
                                    <TableRow>
                                        <TableHead onClick={() => handleSort('documentName')}
                                                   className="cursor-pointer">
                                            Nazwa
                                            dokumentu {sortColumn === 'documentName' && (sortDirection === 'asc' ? '▲' : '▼')}
                                        </TableHead>
                                        <TableHead onClick={() => handleSort('documentType')}
                                                   className="cursor-pointer">
                                            Typ
                                            dokumentu {sortColumn === 'documentType' && (sortDirection === 'asc' ? '▲' : '▼')}
                                        </TableHead>
                                        <TableHead onClick={() => handleSort('documentExtension')}
                                                   className="cursor-pointer">
                                            Typ
                                            pliku {sortColumn === 'documentExtension' && (sortDirection === 'asc' ? '▲' : '▼')}
                                        </TableHead>
                                        <TableHead onClick={() => handleSort('createdAt')} className="cursor-pointer">
                                            Data
                                            dodania {sortColumn === 'createdAt' && (sortDirection === 'asc' ? '▲' : '▼')}
                                        </TableHead>
                                        <TableHead>Akcje</TableHead>
                                    </TableRow>
                                </TableHeader>
                                <TableBody>
                                    {filteredAndSortedDocuments.map((doc) => (
                                        <TableRow key={doc.documentId}>
                                            <TableCell className="font-medium">
                                                <div className="flex items-center">
                                                    {doc.documentName}
                                                </div>
                                            </TableCell>
                                            <TableCell>{doc.documentType}</TableCell>
                                            <TableCell>{doc.documentExtension}</TableCell>
                                            <TableCell>{format(parseISO(doc.createdAt), 'MMMM d, yyyy HH:mm:ss')}</TableCell>
                                            <TableCell>
                                                <Button variant="outline" size="sm"
                                                        onClick={() => handleDownload(doc.documentId, doc.documentName, doc.documentExtension)}>
                                                    <Download className="mr-2 h-4 w-4"/>
                                                    Pobierz plik
                                                </Button>
                                            </TableCell>
                                        </TableRow>
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
                        </>
                    )}
                </CardContent>
            </Card>
        </div>
    )
}