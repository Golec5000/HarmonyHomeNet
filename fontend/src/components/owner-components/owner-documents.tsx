'use client'

import React, {useState, useEffect} from 'react';
import {Card, CardContent, CardHeader, CardTitle} from "@/components/ui/card";
import {Button} from "@/components/ui/button";
import {Table, TableBody, TableCell, TableHead, TableHeader, TableRow} from "@/components/ui/table";
import {FileText, Download, ChevronLeft, ChevronRight} from 'lucide-react';
import {jwtDecode} from "jwt-decode";
import {toast} from "sonner";
import {format, parseISO} from "date-fns";
import {saveAs} from 'file-saver';

interface Document {
    documentId: string;
    documentName: string;
    documentType: string;
    documentExtension: string;
    createdAt: string;
}

interface PageResponse {
    content: Document[];
    totalPages: number;
    totalElements: number;
    size: number;
    number: number;
    hasNext: boolean;
    hasPrev: boolean;
}

export function DocumentsSection() {
    const [documents, setDocuments] = useState<Document[]>([]);
    const [documentIds, setDocumentIds] = useState<string[]>([]);
    const [currentPage, setCurrentPage] = useState(0);
    const [totalPages, setTotalPages] = useState(0);
    const [isLoading, setIsLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);

    const fetchDocuments = async (page: number) => {
        try {
            const token = localStorage.getItem('jwt_accessToken');
            const userId = token ? (jwtDecode<{ userId: string }>(token)).userId : null;
            const response = await fetch(`http://localhost:8444/bwp/hhn/api/v1/document/get-all-documents-by-user-id?userId=${userId}&pageNo=${page}&pageSize=5`, {
                headers: {
                    'Authorization': `Bearer ${token}`,
                },
            });

            if (response.ok) {
                const data: PageResponse = await response.json();
                setDocuments(data.content);
                setDocumentIds(data.content.map(doc => doc.documentId)); // Store document IDs
                setTotalPages(data.totalPages);
            } else if (response.status === 401 || response.status === 403) {
                window.location.href = '/login';
            } else {
                console.error('Failed to fetch documents:', response.statusText);
            }
        } catch (err) {
            setError('Error fetching documents. Please try again later.');
        } finally {
            setIsLoading(false);
        }
    };

    useEffect(() => {
        fetchDocuments(currentPage);
    }, [currentPage]);

    const handlePrevious = () => {
        if (currentPage > 0) {
            setCurrentPage((prev) => prev - 1);
        }
    };

    const handleNext = () => {
        if (currentPage < totalPages - 1) {
            setCurrentPage((prev) => prev + 1);
        }
    };

    const handleDownload = async (documentId: string, documentName: string, documentExtention : string) => {
        try {
            const token = localStorage.getItem('jwt_accessToken');
            const response = await fetch(`http://localhost:8444/bwp/hhn/api/v1/document/download-document?documentId=${documentId}`, {
                headers: {
                    'Authorization': `Bearer ${token}`
                }
            });

            if (response.ok) {
                const blob = await response.blob();
                saveAs(blob, documentName + '.' + documentExtention);
                toast.success('Document downloaded successfully');
            } else if (response.status === 401 || response.status === 403) {
                window.location.href = '/login';
            } else {
                console.error('Failed to fetch documents:', response.statusText);
            }
        } catch (error) {
            console.error('Error downloading document:', error);
            toast.error('Failed to download document. Please try again.');
        }
    };

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
                            <Table>
                                <TableHeader>
                                    <TableRow>
                                        <TableHead>Nazwa dokumenty</TableHead>
                                        <TableHead>Typ dokumentu</TableHead>
                                        <TableHead>Typ pliku</TableHead>
                                        <TableHead>Data dodania</TableHead>
                                    </TableRow>
                                </TableHeader>
                                <TableBody>
                                    {documents.map((doc) => (
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
                                    Nasępna
                                    <ChevronRight className="h-4 w-4"/>
                                </Button>
                            </div>
                        </>
                    )}
                </CardContent>
            </Card>
        </div>
    );
}