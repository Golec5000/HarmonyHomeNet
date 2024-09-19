'use client'

import React, {useState} from 'react'
import {Card, CardContent, CardHeader, CardTitle} from "@/components/ui/card"
import {Button} from "@/components/ui/button"
import {Table, TableBody, TableCell, TableHead, TableHeader, TableRow} from "@/components/ui/table"
import {Vote, Calendar, ChevronDown, ChevronUp, CheckCircle2, XCircle} from 'lucide-react'
import {Progress} from "@/components/ui/progress"
import {toast} from "sonner";

// Simulated database of voting items
const votingData = [
    {
        id: 1,
        title: 'Playground Renovation',
        description: 'Proposal to renovate the community playground with new equipment and safety features.',
        startDate: '2023-07-01',
        endDate: '2023-07-15',
        status: 'Active',
        votes: {
            yes: 45,
            no: 15,
            total: 100
        },
        details: [
            {item: 'New swing set', cost: '$5,000'},
            {item: 'Rubber flooring', cost: '$10,000'},
            {item: 'Climbing structure', cost: '$15,000'},
            {item: 'Landscaping', cost: '$5,000'}
        ]
    },
    {
        id: 2,
        title: 'Community Garden Initiative',
        description: 'Proposal to create a community garden in the unused space behind Building C.',
        startDate: '2023-07-10',
        endDate: '2023-07-24',
        status: 'Active',
        votes: {
            yes: 30,
            no: 10,
            total: 100
        },
        details: [
            {item: 'Soil and compost', cost: '$2,000'},
            {item: 'Irrigation system', cost: '$3,000'},
            {item: 'Fencing', cost: '$1,500'},
            {item: 'Garden tools', cost: '$500'}
        ]
    },
    {
        id: 3,
        title: 'Pet Policy Update',
        description: 'Proposal to update the community pet policy, including breed restrictions and pet fees.',
        startDate: '2023-06-15',
        endDate: '2023-06-29',
        status: 'Closed',
        votes: {
            yes: 60,
            no: 40,
            total: 100
        },
        details: [
            {item: 'Increase pet deposit', newAmount: '$500'},
            {item: 'Monthly pet rent', newAmount: '$50'},
            {item: 'Weight limit increase', newLimit: '50 lbs'},
            {item: 'Additional breed restrictions', info: 'See full policy for details'}
        ]
    }
]

export function Voting() {
    const [expandedVote, setExpandedVote] = useState<number | null>(null)

    const toggleExpand = (id: number) => {
        setExpandedVote(expandedVote === id ? null : id)
    }

    const handleVote = (voteType: string) => {
        toast(`Vote ${voteType} submitted successfully!`, {
            description: `Your ${voteType} vote has been recorded.`,
        })
    }

    return (
        <div className="space-y-6 bg-gray-100 dark:bg-gray-900 text-gray-800 dark:text-gray-200">
            <h1 className="text-3xl font-bold text-gray-800 dark:text-gray-100 flex items-center">
                <Vote className="mr-2 h-8 w-8 text-purple-600 dark:text-purple-400"/>
                Community Voting
            </h1>

            <Card className="bg-white dark:bg-gray-800 text-gray-900 dark:text-gray-100">
                <CardHeader>
                    <CardTitle>Active and Recent Votes</CardTitle>
                </CardHeader>
                <CardContent>
                    <Table>
                        <TableHeader>
                            <TableRow>
                                <TableHead>Title</TableHead>
                                <TableHead>Status</TableHead>
                                <TableHead>Dates</TableHead>
                                <TableHead>Progress</TableHead>
                                <TableHead>Action</TableHead>
                            </TableRow>
                        </TableHeader>
                        <TableBody>
                            {votingData.map((vote) => (
                                <React.Fragment key={vote.id}>
                                    <TableRow>
                                        <TableCell className="font-medium">{vote.title}</TableCell>
                                        <TableCell>
                                        <span className={`px-2 py-1 rounded-full text-xs font-semibold ${
                                            vote.status === 'Active' ? 'bg-green-100 dark:bg-green-800 text-green-800 dark:text-green-100' : 'bg-gray-100 dark:bg-gray-800 text-gray-800 dark:text-gray-100'
                                        }`}>
                                            {vote.status}
                                        </span>
                                        </TableCell>
                                        <TableCell>
                                            <div className="flex items-center">
                                                <Calendar className="mr-1 h-4 w-4 text-blue-600 dark:text-blue-400"/>
                                                {vote.startDate} - {vote.endDate}
                                            </div>
                                        </TableCell>
                                        <TableCell>
                                            <div className="w-full">
                                                <Progress value={(vote.votes.yes / vote.votes.total) * 100}
                                                          className="w-full"/>
                                                <div className="flex justify-between text-xs mt-1">
                                                    <span>Yes: {vote.votes.yes}</span>
                                                    <span>No: {vote.votes.no}</span>
                                                </div>
                                            </div>
                                        </TableCell>
                                        <TableCell>
                                            <div className="flex space-x-2">
                                                {vote.status === 'Active' && (
                                                    <>
                                                        <Button variant="outline" size="sm"
                                                                className="flex items-center"
                                                                onClick={() => handleVote('Yes')}>
                                                            <CheckCircle2
                                                                className="mr-1 h-4 w-4 text-green-600 dark:text-green-400"/>
                                                            Yes
                                                        </Button>
                                                        <Button variant="outline" size="sm"
                                                                className="flex items-center"
                                                                onClick={() => handleVote('No')}>
                                                            <XCircle
                                                                className="mr-1 h-4 w-4 text-red-600 dark:text-red-400"/>
                                                            No
                                                        </Button>
                                                    </>
                                                )}
                                                <Button
                                                    variant="ghost"
                                                    size="sm"
                                                    onClick={() => toggleExpand(vote.id)}
                                                >
                                                    {expandedVote === vote.id ? 'Hide' : 'Details'}
                                                    {expandedVote === vote.id ?
                                                        <ChevronUp className="ml-2 h-4 w-4"/> :
                                                        <ChevronDown className="ml-2 h-4 w-4"/>
                                                    }
                                                </Button>
                                            </div>
                                        </TableCell>
                                    </TableRow>
                                    {expandedVote === vote.id && (
                                        <TableRow>
                                            <TableCell colSpan={5}>
                                                <Card className="mt-2 bg-gray-50 dark:bg-gray-700">
                                                    <CardContent className="p-4">
                                                        <h3 className="font-bold mb-2">Proposal Details</h3>
                                                        <p className="mb-4">{vote.description}</p>
                                                        <Table>
                                                            <TableHeader>
                                                                <TableRow>
                                                                    <TableHead>Item</TableHead>
                                                                    <TableHead>Details</TableHead>
                                                                </TableRow>
                                                            </TableHeader>
                                                            <TableBody>
                                                                {vote.details.map((detail, index) => (
                                                                    <TableRow key={index}>
                                                                        <TableCell>{Object.keys(detail)[0]}</TableCell>
                                                                        <TableCell>{Object.values(detail)[1]}</TableCell>
                                                                    </TableRow>
                                                                ))}
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