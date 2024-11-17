'use client'

import React, {useEffect, useState} from 'react'
import {Card, CardContent, CardHeader, CardTitle} from "@/components/ui/card"
import {Button} from "@/components/ui/button"
import {Input} from "@/components/ui/input"
import {Select, SelectContent, SelectItem, SelectTrigger, SelectValue} from "@/components/ui/select"
import {Table, TableBody, TableCell, TableHead, TableHeader, TableRow} from "@/components/ui/table"
import {Label} from "@/components/ui/label"
import {Dialog, DialogContent, DialogHeader, DialogTitle, DialogTrigger} from "@/components/ui/dialog"
import {Switch} from "@/components/ui/switch"
import {FileUploader} from "react-drag-drop-files"
import {ChevronLeft, ChevronRight, Download, FileText, Trash2, Upload} from 'lucide-react'
import {format} from 'date-fns'
import {toast} from 'sonner'
import {saveAs} from 'file-saver'

interface Document {
    documentId: string
    documentName: string
    documentType: 'RESOLUTION' | 'DECISION' | 'PROPERTY_DEED' | 'OTHER'
    createdAt: string
    documentExtension: string
}

const fileTypes = ["PDF", "DOC", "DOCX", "TXT"];

export function DocumentManagementComponent() {
    const [documents, setDocuments] = useState<Document[]>([])
    const [newDocument, setNewDocument] = useState({
        documentType: 'OTHER' as 'RESOLUTION' | 'DECISION' | 'PROPERTY_DEED' | 'OTHER',
        apartmentSignature: '',
    })
    const [file, setFile] = useState<File | null>(null)
    const [currentPage, setCurrentPage] = useState(0)
    const [totalPages, setTotalPages] = useState(0)
    const [isUploadDialogOpen, setIsUploadDialogOpen] = useState(false)
    const [isDeleteDialogOpen, setIsDeleteDialogOpen] = useState(false)
    const [deleteCompletely, setDeleteCompletely] = useState(true)
    const [userIdToDelete, setUserIdToDelete] = useState('')
    const [documentToDelete, setDocumentToDelete] = useState<string | null>(null)

    useEffect(() => {
        fetchDocuments()
    }, [currentPage])

    const fetchDocuments = async () => {
        try {
            const response = await fetch(`http://localhost:8444/bwp/hhn/api/v1/document/get-all-documents?pageNo=${currentPage}&pageSize=10`, {
                headers: {
                    'Authorization': `Bearer ${localStorage.getItem('jwt_accessToken')}`
                }
            })
            if (response.ok) {
                const data = await response.json()
                console.log('Documents:', data)
                setDocuments(data.content)
                setTotalPages(data.totalPages)
            } else {
                toast.error('Failed to fetch documents')
            }
        } catch (error) {
            console.error('Error fetching documents:', error)
            toast.error('An error occurred while fetching documents')
        }
    }

    const handleFileChange = (file: File) => {
        setFile(file)
    }

    const handleInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        setNewDocument({ ...newDocument, [e.target.name]: e.target.value })
    }

    const handleSelectChange = (value: 'RESOLUTION' | 'DECISION' | 'PROPERTY_DEED' | 'OTHER') => {
        setNewDocument({ ...newDocument, documentType: value })
    }

    const handleUpload = async (e: React.FormEvent) => {
        e.preventDefault()
        if (!file) {
            toast.error('Please select a file to upload')
            return
        }
        if (newDocument.documentType === 'PROPERTY_DEED' && !newDocument.apartmentSignature) {
            toast.error('Apartment signature is required for Property Deed documents')
            return
        }

        const formData = new FormData()
        formData.append('file', file)
        formData.append('apartmentSignature', newDocument.apartmentSignature || 'N/A')
        formData.append('documentType', newDocument.documentType)

        try {
            const response = await fetch('http://localhost:8444/bwp/hhn/api/v1/document/upload-document', {
                method: 'POST',
                headers: {
                    'Authorization': `Bearer ${localStorage.getItem('jwt_accessToken')}`
                },
                body: formData
            })

            if (response.ok) {
                toast.success('Document uploaded successfully')
                fetchDocuments()
                setNewDocument({ documentType: 'OTHER', apartmentSignature: '' })
                setFile(null)
                setIsUploadDialogOpen(false)
            } else {
                toast.error('Failed to upload document')
            }
        } catch (error) {
            console.error('Error uploading document:', error)
            toast.error('An error occurred while uploading the document')
        }
    }

    const handleDownload = async (documentId: string, documentName: string, documentExtension: string) => {
        try {
            const response = await fetch(`http://localhost:8444/bwp/hhn/api/v1/document/download-document?documentId=${documentId}`, {
                headers: {
                    'Authorization': `Bearer ${localStorage.getItem('jwt_accessToken')}`
                }
            })

            if (response.ok) {
                const blob = await response.blob()
                saveAs(blob, documentName + '.' + documentExtension)
                toast.success('Document downloaded successfully')
            } else {
                toast.error('Failed to download document')
            }
        } catch (error) {
            console.error('Error downloading document:', error)
            toast.error('An error occurred while downloading the document')
        }
    }

    const handleDelete = async () => {
        if (!documentToDelete) return

        try {
            const response = await fetch('http://localhost:8444/bwp/hhn/api/v1/document/delete-document', {
                method: 'DELETE',
                headers: {
                    'Authorization': `Bearer ${localStorage.getItem('jwt_accessToken')}`,
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({
                    documentId: documentToDelete,
                    userId: deleteCompletely ? null : userIdToDelete,
                    deleteCompletely: deleteCompletely.toString()
                })
            })

            if (response.ok) {
                toast.success('Document deleted successfully')
                fetchDocuments()
                setIsDeleteDialogOpen(false)
                setDocumentToDelete(null)
                setDeleteCompletely(true)
                setUserIdToDelete('')
            } else {
                toast.error('Failed to delete document')
            }
        } catch (error) {
            console.error('Error deleting document:', error)
            toast.error('An error occurred while deleting the document')
        }
    }

    return (
        <Card className="w-full">
            <CardHeader className="flex flex-row items-center justify-between">
                <CardTitle className="text-2xl font-bold flex items-center space-x-2">
                    <FileText className="h-6 w-6 text-primary"/>
                    <span>Document Management</span>
                </CardTitle>
                <Dialog open={isUploadDialogOpen} onOpenChange={setIsUploadDialogOpen}>
                    <DialogTrigger asChild>
                        <Button>
                            <Upload className="mr-2 h-4 w-4" />
                            Upload Document
                        </Button>
                    </DialogTrigger>
                    <DialogContent>
                        <DialogHeader>
                            <DialogTitle>Upload New Document</DialogTitle>
                        </DialogHeader>
                        <form onSubmit={handleUpload} className="space-y-4">
                            <div>
                                <Label htmlFor="documentType">Document Type</Label>
                                <Select onValueChange={handleSelectChange} value={newDocument.documentType}>
                                    <SelectTrigger>
                                        <SelectValue placeholder="Select document type" />
                                    </SelectTrigger>
                                    <SelectContent>
                                        <SelectItem value="RESOLUTION">Resolution</SelectItem>
                                        <SelectItem value="DECISION">Decision</SelectItem>
                                        <SelectItem value="PROPERTY_DEED">Property Deed</SelectItem>
                                        <SelectItem value="OTHER">Other</SelectItem>
                                    </SelectContent>
                                </Select>
                            </div>
                            {newDocument.documentType === 'PROPERTY_DEED' && (
                                <div>
                                    <Label htmlFor="apartmentSignature">Apartment Signature</Label>
                                    <Input
                                        id="apartmentSignature"
                                        name="apartmentSignature"
                                        value={newDocument.apartmentSignature}
                                        onChange={handleInputChange}
                                        required
                                    />
                                </div>
                            )}
                            <div>
                                <Label>Upload File</Label>
                                <FileUploader handleChange={handleFileChange} name="file" types={fileTypes} />
                                {file && <p className="mt-2 text-sm">Selected file: {file.name}</p>}
                            </div>
                            <Button type="submit">Upload</Button>
                        </form>
                    </DialogContent>
                </Dialog>
            </CardHeader>
            <CardContent>
                <Table>
                    <TableHeader>
                        <TableRow>
                            <TableHead>Name</TableHead>
                            <TableHead>Type</TableHead>
                            <TableHead>Document Extension</TableHead>
                            <TableHead>Created At</TableHead>
                            <TableHead>Actions</TableHead>
                        </TableRow>
                    </TableHeader>
                    <TableBody>
                        {documents.map((doc) => (
                            <TableRow key={doc.documentId}>
                                <TableCell>{doc.documentName}</TableCell>
                                <TableCell>{doc.documentType}</TableCell>
                                <TableCell>{doc.documentExtension}</TableCell>
                                <TableCell>{format(new Date(doc.createdAt), 'dd/MM/yyyy HH:mm')}</TableCell>
                                <TableCell>
                                    <Button variant="outline" size="icon"
                                            onClick={() => handleDownload(doc.documentId, doc.documentName, doc.documentExtension)}
                                            className="mr-2">
                                        <Download className="h-4 w-4"/>
                                    </Button>
                                    <Button variant="outline" size="icon" onClick={() => {
                                        setDocumentToDelete(doc.documentId)
                                        setIsDeleteDialogOpen(true)
                                    }}>
                                        <Trash2 className="h-4 w-4"/>
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

            <Dialog open={isDeleteDialogOpen} onOpenChange={setIsDeleteDialogOpen}>
                <DialogContent>
                    <DialogHeader>
                        <DialogTitle>Delete Document</DialogTitle>
                    </DialogHeader>
                    <div className="space-y-4">
                        <div className="flex items-center space-x-2">
                            <Switch
                                id="delete-completely"
                                checked={deleteCompletely}
                                onCheckedChange={setDeleteCompletely}
                            />
                            <Label htmlFor="delete-completely">Delete Completely</Label>
                        </div>
                        {!deleteCompletely && (
                            <div>
                                <Label htmlFor="user-id">User ID</Label>
                                <Input
                                    id="user-id"
                                    value={userIdToDelete}
                                    onChange={(e) => setUserIdToDelete(e.target.value)}
                                    placeholder="Enter user ID"
                                />
                            </div>
                        )}
                        <Button onClick={handleDelete}>Confirm Delete</Button>
                    </div>
                </DialogContent>
            </Dialog>
        </Card>
    )
}