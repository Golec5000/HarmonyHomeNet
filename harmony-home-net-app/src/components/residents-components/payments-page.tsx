'use client'

import React, {useState} from 'react'
import {Card, CardContent, CardHeader, CardTitle} from "@/components/ui/card"
import {Button} from "@/components/ui/button"
import {Table, TableBody, TableCell, TableHead, TableHeader, TableRow} from "@/components/ui/table"
import {CreditCard, DollarSign, Calendar, AlertCircle, ChevronDown, ChevronUp} from 'lucide-react'

// Simulated database of payments with detailed breakdown
const paymentsData = [
    {
        id: 1,
        description: 'Rent - July 2023',
        amount: 1200,
        dueDate: '2023-07-01',
        status: 'Pending',
        breakdown: [
            {item: 'Base Rent', amount: 1000},
            {item: 'Parking Space', amount: 100},
            {item: 'Pet Fee', amount: 50},
            {item: 'Storage Unit', amount: 50}
        ]
    },
    {
        id: 2,
        description: 'Utilities - July 2023',
        amount: 150,
        dueDate: '2023-07-15',
        status: 'Paid',
        breakdown: [
            {item: 'Electricity', amount: 80},
            {item: 'Water', amount: 40},
            {item: 'Gas', amount: 30}
        ]
    },
    {
        id: 3,
        description: 'Parking Fee - July 2023',
        amount: 50,
        dueDate: '2023-07-01',
        status: 'Overdue',
        breakdown: [
            {item: 'Reserved Parking Spot', amount: 50}
        ]
    },
]

export function Payments() {
    const [expandedPayment, setExpandedPayment] = useState<number | null>(null)

    const toggleExpand = (id: number) => {
        setExpandedPayment(expandedPayment === id ? null : id)
    }

    return (
        <div className="space-y-6 bg-gray-100 dark:bg-gray-900 text-gray-800 dark:text-gray-200">
            <h1 className="text-3xl font-bold text-gray-800 dark:text-gray-100 flex items-center">
                <CreditCard className="mr-2 h-8 w-8 text-green-600 dark:text-green-400"/>
                Payments
            </h1>

            <Card className="bg-white dark:bg-gray-800 text-gray-900 dark:text-gray-100">
                <CardHeader>
                    <CardTitle>Payment History</CardTitle>
                </CardHeader>
                <CardContent>
                    <Table>
                        <TableHeader>
                            <TableRow>
                                <TableHead>Description</TableHead>
                                <TableHead>Amount</TableHead>
                                <TableHead>Due Date</TableHead>
                                <TableHead>Status</TableHead>
                                <TableHead>Action</TableHead>
                            </TableRow>
                        </TableHeader>
                        <TableBody>
                            {paymentsData.map((payment) => (
                                <React.Fragment key={payment.id}>
                                    <TableRow>
                                        <TableCell className="font-medium">{payment.description}</TableCell>
                                        <TableCell>
                                            <div className="flex items-center">
                                                <DollarSign
                                                    className="mr-1 h-4 w-4 text-green-600 dark:text-green-400"/>
                                                {payment.amount}
                                            </div>
                                        </TableCell>
                                        <TableCell>
                                            <div className="flex items-center">
                                                <Calendar className="mr-1 h-4 w-4 text-blue-600 dark:text-blue-400"/>
                                                {payment.dueDate}
                                            </div>
                                        </TableCell>
                                        <TableCell>
                                            <div className="flex items-center">
                                                <AlertCircle
                                                    className={`mr-1 h-4 w-4 ${payment.status === 'Paid' ? 'text-green-600 dark:text-green-400' : payment.status === 'Overdue' ? 'text-red-600 dark:text-red-400' : 'text-yellow-600 dark:text-yellow-400'}`}/>
                                                {payment.status}
                                            </div>
                                        </TableCell>
                                        <TableCell>
                                            <div className="flex space-x-2">
                                                <Button variant="outline" size="sm">Pay Now</Button>
                                                <Button
                                                    variant="ghost"
                                                    size="sm"
                                                    onClick={() => toggleExpand(payment.id)}
                                                >
                                                    {expandedPayment === payment.id ? 'Hide' : 'Details'}
                                                    {expandedPayment === payment.id ?
                                                        <ChevronUp className="ml-2 h-4 w-4"/> :
                                                        <ChevronDown className="ml-2 h-4 w-4"/>
                                                    }
                                                </Button>
                                            </div>
                                        </TableCell>
                                    </TableRow>
                                    {expandedPayment === payment.id && (
                                        <TableRow>
                                            <TableCell colSpan={5}>
                                                <Card className="mt-2 bg-gray-50 dark:bg-gray-700">
                                                    <CardContent className="p-4">
                                                        <h3 className="font-bold mb-2">Payment Breakdown</h3>
                                                        <Table>
                                                            <TableHeader>
                                                                <TableRow>
                                                                    <TableHead>Item</TableHead>
                                                                    <TableHead>Amount</TableHead>
                                                                </TableRow>
                                                            </TableHeader>
                                                            <TableBody>
                                                                {payment.breakdown.map((item, index) => (
                                                                    <TableRow key={index}>
                                                                        <TableCell>{item.item}</TableCell>
                                                                        <TableCell>${item.amount}</TableCell>
                                                                    </TableRow>
                                                                ))}
                                                                <TableRow>
                                                                    <TableCell className="font-bold">Total</TableCell>
                                                                    <TableCell
                                                                        className="font-bold">${payment.amount}</TableCell>
                                                                </TableRow>
                                                            </TableBody>
                                                        </Table>
                                                    </CardContent>
                                                </Card>
                                            </TableCell>
                                        </TableRow>
                                    )}
                                </React.Fragment>
                            ))}
                        </TableBody>
                    </Table>
                </CardContent>
            </Card>
        </div>
    )
}