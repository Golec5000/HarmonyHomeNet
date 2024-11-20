'use client'

import React, {useCallback, useEffect, useState} from 'react';
import {Card, CardContent, CardHeader, CardTitle} from "@/components/ui/card";
import {Button} from "@/components/ui/button";
import {Table, TableBody, TableCell, TableHead, TableHeader, TableRow} from "@/components/ui/table";
import {
    AlertCircle,
    Calendar,
    ChevronDown,
    ChevronLeft,
    ChevronRight,
    ChevronUp,
    CreditCard,
    DollarSign
} from 'lucide-react';
import {format, parseISO} from 'date-fns';
import {Collapsible, CollapsibleContent, CollapsibleTrigger} from "@/components/ui/collapsible";
import {toast} from 'sonner';

interface PaymentComponentResponse {
    id: number;
    componentType: string;
    unitPrice: number;
    specialMultiplier: number;
    componentAmount: number;
    createdAt: string;
    updatedAt: string;
    unit: string;
}

interface PaymentResponse {
    paymentId: string;
    paymentStatus: 'PENDING' | 'PAID' | 'OVERDUE';
    paymentDate: string;
    paymentTime: string;
    paymentAmount: number;
    createdAt: string;
    description: string;
    components?: PaymentComponentResponse[];
    readyToPay: boolean;
}

interface PageResponse {
    currentPage: number;
    pageSize: number;
    totalPages: number;
    content: PaymentResponse[];
    last: boolean;
    hasNext: boolean;
    hasPrevious: boolean;
}

interface PaymentsProps {
    apartmentSignature: string | null;
}

export function Payments({apartmentSignature}: PaymentsProps) {
    const [payments, setPayments] = useState<PageResponse | null>(null);
    const [expandedPayment, setExpandedPayment] = useState<string | null>(null);
    const [isLoading, setIsLoading] = useState(true);

    const fetchPayments = useCallback(async (page: number) => {
        setIsLoading(true);
        try {
            const response = await fetch(`http://localhost:8444/bwp/hhn/api/v1/payment/get-payment-by-apartment?apartmentSignature=${apartmentSignature}&pageNo=${page}&pageSize=5`, {
                headers: {
                    'Authorization': `Bearer ${sessionStorage.getItem('jwt_accessToken')}`
                },
            });
            if (response.ok) {
                const data: PageResponse = await response.json();
                setPayments(data);
            } else if (response.status === 401 || response.status === 403) {
                window.location.href = '/login';
            } else {
                console.error('Failed to fetch payments:', response.statusText);
            }
        } catch (error) {
            console.error('Error fetching payments:', error);
        } finally {
            setIsLoading(false);
        }
    }, [apartmentSignature]);

    useEffect(() => {
        if (apartmentSignature) {
            fetchPayments(0);
        }
    }, [apartmentSignature, fetchPayments]);

    const fetchPaymentComponents = async (paymentId: string) => {
        try {
            const response = await fetch(`http://localhost:8444/bwp/hhn/api/v1/payment/get-payment-components?paymentId=${paymentId}`, {
                headers: {
                    'Authorization': `Bearer ${sessionStorage.getItem('jwt_accessToken')}`
                },
            });
            if (response.ok) {
                const components: PaymentComponentResponse[] = await response.json();
                setPayments((prevPayments) => {
                    if (!prevPayments) return prevPayments;
                    return {
                        ...prevPayments,
                        content: prevPayments.content.map((payment) =>
                            payment.paymentId === paymentId ? {...payment, components} : payment
                        ),
                    };
                });
            } else {
                console.error('Failed to fetch payment components:', response.statusText);
            }
        } catch (error) {
            console.error('Error fetching payment components:', error);
        }
    };

    const toggleExpand = (id: string) => {
        if (expandedPayment === id) {
            setExpandedPayment(null);
            setPayments((prevPayments) => {
                if (!prevPayments) return prevPayments;
                return {
                    ...prevPayments,
                    content: prevPayments.content.map((payment) =>
                        payment.paymentId === id ? {...payment, components: undefined} : payment
                    ),
                };
            });
        } else {
            setExpandedPayment(id);
            fetchPaymentComponents(id);
        }
    };

    const handlePayPayment = async (paymentId: string) => {
        try {
            const response = await fetch(`http://localhost:8444/bwp/hhn/api/v1/payment/pay?paymentId=${paymentId}`, {
                method: 'POST',
                headers: {
                    'Authorization': `Bearer ${sessionStorage.getItem('jwt_accessToken')}`
                }
            });
            if (response.ok) {
                toast.success('Payment successful');
                fetchPayments(0); // Refresh the payments list
            } else {
                toast.error('Failed to process payment');
            }
        } catch (error) {
            console.error('Error processing payment:', error);
            toast.error('An error occurred while processing the payment');
        }
    };

    const handlePrevious = () => {
        if (payments && payments.hasPrevious) {
            fetchPayments(payments.currentPage - 1);
        }
    };

    const handleNext = () => {
        if (payments && payments.hasNext) {
            fetchPayments(payments.currentPage + 1);
        }
    };

    const getStatusColor = (status: string) => {
        switch (status) {
            case 'PAID':
                return 'text-green-600 dark:text-green-400';
            case 'OVERDUE':
                return 'text-red-600 dark:text-red-400';
            default:
                return 'text-yellow-600 dark:text-yellow-400';
        }
    };

    return (
        <div className="space-y-6 bg-background text-foreground">
            <h1 className="text-3xl font-bold flex items-center">
                <CreditCard className="mr-2 h-8 w-8 text-primary"/>
                Payments
            </h1>

            <Card>
                <CardHeader>
                    <CardTitle>Payment History</CardTitle>
                </CardHeader>
                <CardContent>
                    <Table>
                        <TableHeader>
                            <TableRow>
                                <TableHead>Description</TableHead>
                                <TableHead>Amount</TableHead>
                                <TableHead>Time to Pay</TableHead>
                                <TableHead>Payed</TableHead>
                                <TableHead>Status</TableHead>
                                <TableHead>Actions</TableHead>
                            </TableRow>
                        </TableHeader>
                        <TableBody>
                            {isLoading ? (
                                <TableRow>
                                    <TableCell colSpan={6} className="text-center">Loading payments...</TableCell>
                                </TableRow>
                            ) : payments && payments.content.length > 0 ? (
                                payments.content.map((payment) => (
                                    <React.Fragment key={payment.paymentId}>
                                        <TableRow>
                                            <TableCell className="font-medium">{payment.description}</TableCell>
                                            <TableCell>
                                                <div className="flex items-center">
                                                    <DollarSign className="mr-1 h-4 w-4 text-primary"/>
                                                    {payment.paymentAmount.toFixed(2)}
                                                </div>
                                            </TableCell>
                                            <TableCell>
                                                <div className="flex items-center">
                                                    <Calendar className="mr-1 h-4 w-4 text-primary"/>
                                                    {payment.createdAt ? format(parseISO(payment.createdAt), 'MMMM d, yyyy HH:mm:ss') : 'N/A'}
                                                </div>
                                            </TableCell>
                                            <TableCell>
                                                <div className="flex items-center">
                                                    <Calendar className="mr-1 h-4 w-4 text-primary"/>
                                                    {payment.paymentTime ? format(parseISO(payment.paymentTime), 'MMMM d, yyyy HH:mm:ss') : 'N/A'}
                                                </div>
                                            </TableCell>
                                            <TableCell>
                                                <div className="flex items-center">
                                                    <AlertCircle
                                                        className={`mr-1 h-4 w-4 ${getStatusColor(payment.paymentStatus)}`}/>
                                                    {payment.paymentStatus}
                                                </div>
                                            </TableCell>
                                            <TableCell>
                                                <div className="flex space-x-2">
                                                    {payment.paymentStatus !== 'PAID' && (
                                                        <Button
                                                            variant="outline"
                                                            size="sm"
                                                            disabled={!payment.readyToPay}
                                                            onClick={() => handlePayPayment(payment.paymentId)}
                                                        >
                                                            Pay Now
                                                        </Button>
                                                    )}
                                                    <Collapsible>
                                                        <CollapsibleTrigger asChild>
                                                            <Button
                                                                variant="ghost"
                                                                size="sm"
                                                                onClick={() => toggleExpand(payment.paymentId)}
                                                            >
                                                                {expandedPayment === payment.paymentId ? 'Hide' : 'Details'}
                                                                {expandedPayment === payment.paymentId ?
                                                                    <ChevronUp className="ml-2 h-4 w-4"/> :
                                                                    <ChevronDown className="ml-2 h-4 w-4"/>
                                                                }
                                                            </Button>
                                                        </CollapsibleTrigger>
                                                        <CollapsibleContent>
                                                            <Card className="mt-2 bg-muted">
                                                                <CardContent className="p-4">
                                                                    <h3 className="font-bold mb-2">Payment
                                                                        Components</h3>
                                                                    <Table>
                                                                        <TableHeader>
                                                                            <TableRow>
                                                                                <TableHead>Component</TableHead>
                                                                                <TableHead>Unit Price</TableHead>
                                                                                <TableHead>Unit</TableHead>
                                                                                <TableHead>Multiplier</TableHead>
                                                                                <TableHead>Amount</TableHead>
                                                                            </TableRow>
                                                                        </TableHeader>
                                                                        <TableBody>
                                                                            {payment.components?.map((component) => (
                                                                                <TableRow key={component.id}>
                                                                                    <TableCell>{component.componentType}</TableCell>
                                                                                    <TableCell>{component.unitPrice.toFixed(2)}</TableCell>
                                                                                    <TableCell>{component.unit}</TableCell>
                                                                                    <TableCell>{component.specialMultiplier}</TableCell>
                                                                                    <TableCell>{component.componentAmount.toFixed(2)}</TableCell>
                                                                                </TableRow>
                                                                            ))}
                                                                            <TableRow>
                                                                                <TableCell
                                                                                    className="font-bold">Total</TableCell>
                                                                                <TableCell colSpan={3}
                                                                                           className="font-bold text-right">
                                                                                    ${payment.paymentAmount.toFixed(2)}
                                                                                </TableCell>
                                                                            </TableRow>
                                                                        </TableBody>
                                                                    </Table>
                                                                </CardContent>
                                                            </Card>
                                                        </CollapsibleContent>
                                                    </Collapsible>
                                                </div>
                                            </TableCell>
                                        </TableRow>
                                    </React.Fragment>
                                ))
                            ) : (
                                <TableRow>
                                    <TableCell colSpan={6} className="text-center">No payments found.</TableCell>
                                </TableRow>
                            )}
                        </TableBody>
                    </Table>
                    <div className="flex justify-between items-center mt-4">
                        <Button
                            variant="outline"
                            size="sm"
                            onClick={handlePrevious}
                            disabled={!payments || !payments.hasPrevious}
                        >
                            <ChevronLeft className="h-4 w-4 mr-2"/>
                            Previous
                        </Button>
                        <span className="text-sm">
                            Page {payments ? payments.currentPage + 1 : 0} of {payments ? payments.totalPages : 0}
                        </span>
                        <Button
                            variant="outline"
                            size="sm"
                            onClick={handleNext}
                            disabled={!payments || !payments.hasNext}
                        >
                            Next
                            <ChevronRight className="h-4 w-4 ml-2"/>
                        </Button>
                    </div>
                </CardContent>
            </Card>
        </div>
    );
}