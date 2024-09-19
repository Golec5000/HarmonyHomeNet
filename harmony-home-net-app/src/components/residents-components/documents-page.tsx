'use client'

import React from 'react'
import {Card, CardContent, CardHeader, CardTitle} from "@/components/ui/card"
import {Button} from "@/components/ui/button"
import {Table, TableBody, TableCell, TableHead, TableHeader, TableRow} from "@/components/ui/table"
import {FileText, File, Download, Upload} from 'lucide-react'

export function Documents() {
    const documents = [
        {id: 1, name: 'Lease Agreement', type: 'PDF', date: '2023-06-01'},
        {id: 2, name: 'House Rules', type: 'PDF', date: '2023-06-15'},
        {id: 3, name: 'Maintenance Schedule', type: 'XLSX', date: '2023-07-01'},
    ]

    return (
        <div className="space-y-6 bg-gray-100 dark:bg-gray-900 text-gray-800 dark:text-gray-200">
            <h1 className="text-3xl font-bold text-gray-800 dark:text-gray-100 flex items-center">
                <FileText className="mr-2 h-8 w-8 text-blue-600 dark:text-blue-400"/>
                Documents
            </h1>

            <Card className="bg-white dark:bg-gray-800 text-gray-900 dark:text-gray-100">
                <CardHeader className="flex justify-between items-center">
                    <CardTitle>Your Documents</CardTitle>
                    <Button variant="outline" size="sm">
                        <Upload className="mr-2 h-4 w-4"/>
                        Upload New Document
                    </Button>
                </CardHeader>
                <CardContent>
                    <Table>
                        <TableHeader>
                            <TableRow>
                                <TableHead>Name</TableHead>
                                <TableHead>Type</TableHead>
                                <TableHead>Date</TableHead>
                                <TableHead>Action</TableHead>
                            </TableRow>
                        </TableHeader>
                        <TableBody>
                            {documents.map((doc) => (
                                <TableRow key={doc.id}>
                                    <TableCell className="font-medium">
                                        <div className="flex items-center">
                                            <File className="mr-2 h-4 w-4 text-blue-600 dark:text-blue-400"/>
                                            {doc.name}
                                        </div>
                                    </TableCell>
                                    <TableCell>{doc.type}</TableCell>
                                    <TableCell>{doc.date}</TableCell>
                                    <TableCell>
                                        <Button variant="outline" size="sm">
                                            <Download className="mr-2 h-4 w-4"/>
                                            Download
                                        </Button>
                                    </TableCell>
                                </TableRow>
                            ))}
                        </TableBody>
                    </Table>
                </CardContent>
            </Card>
        </div>
    )
}