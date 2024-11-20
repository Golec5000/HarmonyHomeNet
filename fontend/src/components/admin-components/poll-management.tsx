'use client';

import React, {useCallback, useEffect, useState} from 'react';
import {Card, CardContent, CardHeader, CardTitle} from "@/components/ui/card";
import {Button} from "@/components/ui/button";
import {Input} from "@/components/ui/input";
import {Table, TableBody, TableCell, TableHead, TableHeader, TableRow} from "@/components/ui/table";
import {Label} from "@/components/ui/label";
import {Dialog, DialogContent, DialogHeader, DialogTitle, DialogTrigger} from "@/components/ui/dialog";
import {FileUploader} from "react-drag-drop-files";
import {
    Briefcase,
    Calendar,
    ChevronDown,
    ChevronLeft,
    ChevronRight,
    ChevronUp,
    Download,
    FileText,
    Percent,
    StopCircle,
    ThumbsDown,
    ThumbsUp,
    Trash2,
    Upload
} from 'lucide-react';
import {format} from 'date-fns';
import {toast} from 'sonner';
import {saveAs} from 'file-saver';
import {Collapsible, CollapsibleContent} from "@/components/ui/collapsible";
import {jwtDecode} from 'jwt-decode';

interface Poll {
    id: string;
    pollName: string;
    content: string;
    createdAt: string;
    endDate: string;
    summary: number;
    fileName: string;
    fileExtension: string;
    minSummary: number;
    currentVotesCount: number;
    minCurrentVotesCount: number;
    isActive: boolean;
}

interface Vote {
    id: number;
    voteChoice: 'FOR' | 'AGAINST' | 'PASS';
    createdAt: string;
    apartmentSignature: string;
}

interface jwtCustomClaims {
    userId: string;
}

const fileTypes = ["PDF", "DOC", "DOCX", "TXT"];

export function PollManagementComponent() {
    const [polls, setPolls] = useState<Poll[]>([]);
    const [newPoll, setNewPoll] = useState({
        pollName: '',
        content: '',
        endDate: '',
        minSummary: '',
        minCurrentVotesCount: ''
    });
    const [file, setFile] = useState<File | null>(null);
    const [currentPage, setCurrentPage] = useState(0);
    const [totalPages, setTotalPages] = useState(0);
    const [isCreateDialogOpen, setIsCreateDialogOpen] = useState(false);
    const [expandedPoll, setExpandedPoll] = useState<string | null>(null);
    const [votes, setVotes] = useState<Vote[]>([]);

    const fetchPolls = useCallback(async (currentPage: number) => {
        try {
            const response = await fetch(`http://localhost:8444/bwp/hhn/api/v1/poll/get-all-polls?pageNo=${currentPage}&pageSize=10`, {
                headers: {
                    'Authorization': `Bearer ${sessionStorage.getItem('jwt_accessToken')}`
                }
            });
            if (response.ok) {
                const data = await response.json();
                setPolls(data.content);
                setTotalPages(data.totalPages);
            } else {
                toast.error('Failed to fetch polls');
            }
        } catch (error) {
            console.error('Error fetching polls:', error);
            toast.error('An error occurred while fetching polls');
        }
    }, []);

    useEffect(() => {
        fetchPolls(currentPage);
    }, [currentPage, fetchPolls]);

    const handleFileChange = (file: File) => {
        setFile(file);
    };

    const handleInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        setNewPoll({...newPoll, [e.target.name]: e.target.value});
    };

    const handleCreatePoll = async (e: React.FormEvent) => {
        e.preventDefault();

        if (!file) {
            toast.error("Please select a file to upload");
            return;
        }

        const token = sessionStorage.getItem("jwt_accessToken");
        if (!token) {
            toast.error("User not authenticated");
            return;
        }

        const decodedToken = jwtDecode<jwtCustomClaims>(token);
        const employeeId = decodedToken.userId;

        const formattedEndDate = new Date(newPoll.endDate).toISOString();

        const formData = new FormData();
        formData.append("file", file);
        formData.append("pollName", newPoll.pollName);
        formData.append("content", newPoll.content);
        formData.append("endDate", formattedEndDate);
        formData.append("minSummary", newPoll.minSummary);
        formData.append("minCurrentVotesCount", newPoll.minCurrentVotesCount);

        try {
            const response = await fetch(
                `http://localhost:8444/bwp/hhn/api/v1/poll/create-poll/${employeeId}`,
                {
                    method: "POST",
                    headers: {
                        Authorization: `Bearer ${token}`,
                    },
                    body: formData,
                }
            );

            if (response.ok) {
                toast.success("Poll created successfully");
                fetchPolls(currentPage);
                setNewPoll({pollName: "", content: "", endDate: "", minSummary: "", minCurrentVotesCount: ""});
                setFile(null);
                setIsCreateDialogOpen(false);
            } else {
                const errorData = await response.json();
                toast.error(`Failed to create poll: ${errorData.message || "Unknown error"}`);
            }
        } catch (error) {
            console.error("Error creating poll:", error);
            toast.error("An error occurred while creating the poll");
        }
    };

    const handleDownload = async (pollId: string, pollName: string, pollFileExtension: string) => {
        try {
            const response = await fetch(`http://localhost:8444/bwp/hhn/api/v1/poll/download-poll/${pollId}`, {
                headers: {
                    'Authorization': `Bearer ${sessionStorage.getItem('jwt_accessToken')}`
                }
            });

            if (response.ok) {
                const blob = await response.blob();
                saveAs(blob, `${pollName}.${pollFileExtension}`);
                toast.success('Poll file downloaded successfully');
            } else {
                toast.error('Failed to download poll file');
            }
        } catch (error) {
            console.error('Error downloading poll file:', error);
            toast.error('An error occurred while downloading the poll file');
        }
    };

    const handleDelete = async (pollId: string) => {
        try {
            const response = await fetch(`http://localhost:8444/bwp/hhn/api/v1/poll/delete-poll?pollId=${pollId}`, {
                method: 'DELETE',
                headers: {
                    'Authorization': `Bearer ${sessionStorage.getItem('jwt_accessToken')}`
                }
            });

            if (response.ok) {
                toast.success('Poll deleted successfully');
                fetchPolls(currentPage);
            } else {
                toast.error('Failed to delete poll');
            }
        } catch (error) {
            console.error('Error deleting poll:', error);
            toast.error('An error occurred while deleting the poll');
        }
    };

    const fetchVotes = async (pollId: string) => {
        try {
            const response = await fetch(`http://localhost:8444/bwp/hhn/api/v1/poll/get-votes-from-poll?pollId=${pollId}&pageNo=0&pageSize=100`, {
                headers: {
                    'Authorization': `Bearer ${sessionStorage.getItem('jwt_accessToken')}`
                }
            });
            if (response.ok) {
                const data = await response.json();
                setVotes(data.content);
            } else {
                toast.error('Failed to fetch votes');
            }
        } catch (error) {
            console.error('Error fetching votes:', error);
            toast.error('An error occurred while fetching votes');
        }
    };

    const handleDeleteVote = async (voteId: number) => {
        try {
            const response = await fetch(`http://localhost:8444/bwp/hhn/api/v1/poll/delete-vote/${voteId}`, {
                method: 'PUT',
                headers: {
                    'Authorization': `Bearer ${sessionStorage.getItem('jwt_accessToken')}`
                }
            });

            if (response.ok) {
                toast.success('Vote deleted successfully');
                fetchVotes(expandedPoll!);
            } else {
                toast.error('Failed to delete vote');
            }
        } catch (error) {
            console.error('Error deleting vote:', error);
            toast.error('An error occurred while deleting the vote');
        }
    };

    return (
        <Card className="w-full">
            <CardHeader className="flex flex-row items-center justify-between">
                <CardTitle className="text-2xl font-bold flex items-center space-x-2">
                    <Briefcase className="h-6 w-6 text-primary"/>
                    <span>Poll Management</span>
                </CardTitle>
                <Dialog open={isCreateDialogOpen} onOpenChange={setIsCreateDialogOpen}>
                    <DialogTrigger asChild>
                        <Button>
                            <Upload className="mr-2 h-4 w-4"/>
                            Create Poll
                        </Button>
                    </DialogTrigger>
                    <DialogContent>
                        <DialogHeader>
                            <DialogTitle>Create New Poll</DialogTitle>
                        </DialogHeader>
                        <form onSubmit={handleCreatePoll} className="space-y-4">
                            <div>
                                <Label htmlFor="pollName">Poll Name</Label>
                                <Input
                                    id="pollName"
                                    name="pollName"
                                    value={newPoll.pollName}
                                    onChange={handleInputChange}
                                    required
                                />
                            </div>
                            <div>
                                <Label htmlFor="content">Content</Label>
                                <Input
                                    id="content"
                                    name="content"
                                    value={newPoll.content}
                                    onChange={handleInputChange}
                                    required
                                />
                            </div>
                            <div>
                                <Label htmlFor="endDate">End Date</Label>
                                <Input
                                    id="endDate"
                                    name="endDate"
                                    type="datetime-local"
                                    value={newPoll.endDate}
                                    onChange={handleInputChange}
                                    required
                                />
                            </div>
                            <div>
                                <Label htmlFor="minSummary">Minimum Summary</Label>
                                <Input
                                    id="minSummary"
                                    name="minSummary"
                                    type="number"
                                    value={newPoll.minSummary}
                                    onChange={handleInputChange}
                                    required
                                />
                            </div>
                            <div>
                                <Label htmlFor="minCurrentVotesCount">Minimum Votes Count</Label>
                                <Input
                                    id="minCurrentVotesCount"
                                    name="minCurrentVotesCount"
                                    type="number"
                                    value={newPoll.minCurrentVotesCount}
                                    onChange={handleInputChange}
                                    required
                                />
                            </div>
                            <div>
                                <Label>Upload File</Label>
                                <FileUploader handleChange={handleFileChange} name="file" types={fileTypes}/>
                                {file && <p className="mt-2 text-sm">Selected file: {file.name}</p>}
                            </div>
                            <Button type="submit">Create Poll</Button>
                        </form>
                    </DialogContent>
                </Dialog>
            </CardHeader>
            <CardContent>
                <Table>
                    <TableHeader>
                        <TableRow>
                            <TableHead><FileText className="h-4 w-4 mr-2 inline-block"/>Name</TableHead>
                            <TableHead><Calendar className="h-4 w-4 mr-2 inline-block"/>Created At</TableHead>
                            <TableHead><Calendar className="h-4 w-4 mr-2 inline-block"/>End Date</TableHead>
                            <TableHead><Percent className="h-4 w-4 mr-2 inline-block"/>Min Summary</TableHead>
                            <TableHead><Percent className="h-4 w-4 mr-2 inline-block"/>Summary</TableHead>
                            <TableHead>Min Votes</TableHead>
                            <TableHead>Current Votes</TableHead>
                            <TableHead>Actions</TableHead>
                        </TableRow>
                    </TableHeader>
                    <TableBody>
                        {polls.map((poll) => (
                            <React.Fragment key={poll.id}>
                                <TableRow>
                                    <TableCell>{poll.pollName}</TableCell>
                                    <TableCell>{format(new Date(poll.createdAt), 'dd/MM/yyyy HH:mm')}</TableCell>
                                    <TableCell>{format(new Date(poll.endDate), 'dd/MM/yyyy HH:mm')}</TableCell>
                                    <TableCell>{poll.summary}</TableCell>
                                    <TableCell>{poll.minSummary}</TableCell>
                                    <TableCell>{poll.currentVotesCount}</TableCell>
                                    <TableCell>{poll.minCurrentVotesCount}</TableCell>
                                    <TableCell>
                                        <Button variant="outline" size="icon"
                                                onClick={() => handleDownload(poll.id, poll.fileName, poll.fileExtension)}
                                                className="mr-2">
                                            <Download className="h-4 w-4"/>
                                        </Button>
                                        <Button variant="outline" size="icon" onClick={() => handleDelete(poll.id)}
                                                className="mr-2">
                                            <Trash2 className="h-4 w-4"/>
                                        </Button>
                                        <Button
                                            variant="outline"
                                            size="icon"
                                            onClick={() => {
                                                if (expandedPoll === poll.id) {
                                                    setExpandedPoll(null);
                                                } else {
                                                    setExpandedPoll(poll.id);
                                                    fetchVotes(poll.id);
                                                }
                                            }}
                                        >
                                            {expandedPoll === poll.id ? <ChevronUp className="h-4 w-4"/> :
                                                <ChevronDown className="h-4 w-4"/>}
                                        </Button>
                                    </TableCell>
                                </TableRow>
                                <TableRow>
                                    <TableCell colSpan={8}>
                                        <Collapsible open={expandedPoll === poll.id}>
                                            <CollapsibleContent>
                                                <Table>
                                                    <TableHeader>
                                                        <TableRow>
                                                            <TableHead><ThumbsUp className="h-4 w-4 mr-2 inline-block"/>Vote
                                                                Choice</TableHead>
                                                            <TableHead><Calendar className="h-4 w-4 mr-2 inline-block"/>Created
                                                                At</TableHead>
                                                            <TableHead><FileText className="h-4 w-4 mr-2 inline-block"/>Apartment
                                                                Signature</TableHead>
                                                            <TableHead>Action</TableHead>
                                                        </TableRow>
                                                    </TableHeader>
                                                    <TableBody>
                                                        {votes.map((vote) => (
                                                            <TableRow key={vote.id}>
                                                                <TableCell>
                                                                    {vote.voteChoice === 'FOR' ? (
                                                                        <><ThumbsUp
                                                                            className="h-4 w-4 mr-2 inline-block text-green-500"/>{vote.voteChoice}</>
                                                                    ) : vote.voteChoice === 'AGAINST' ? (
                                                                        <><ThumbsDown
                                                                            className="h-4 w-4 mr-2 inline-block text-red-500"/>{vote.voteChoice}</>
                                                                    ) : (
                                                                        <><StopCircle
                                                                            className="h-4 w-4 mr-2 inline-block text-yellow-500"/>{vote.voteChoice}</>
                                                                    )}
                                                                </TableCell>
                                                                <TableCell>{format(new Date(vote.createdAt), 'dd/MM/yyyy HH:mm')}</TableCell>
                                                                <TableCell>{vote.apartmentSignature}</TableCell>
                                                                <TableCell>
                                                                    <Button variant="outline" size="icon"
                                                                            onClick={() => handleDeleteVote(vote.id)}>
                                                                        <Trash2 className="h-4 w-4"/>
                                                                    </Button>
                                                                </TableCell>
                                                            </TableRow>
                                                        ))}
                                                    </TableBody>
                                                </Table>
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
    );
}