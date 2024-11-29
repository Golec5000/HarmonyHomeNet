'use client'

import React, {useCallback, useEffect, useState} from 'react'
import {format} from 'date-fns'
import {toast} from 'sonner'
import {Card, CardContent, CardHeader, CardTitle} from "@/components/ui/card"
import {Button} from "@/components/ui/button"
import {Input} from "@/components/ui/input"
import {Textarea} from "@/components/ui/textarea"
import {Dialog, DialogContent, DialogHeader, DialogTitle, DialogTrigger} from "@/components/ui/dialog"
import {Collapsible, CollapsibleContent,} from "@/components/ui/collapsible"
import {ScrollArea} from "@/components/ui/scroll-area"
import {ChevronDown, ChevronLeft, ChevronRight, ChevronUp, MessageSquare, Plus, Trash2} from 'lucide-react'
import {jwtDecode} from "jwt-decode";

interface JwtPayload {
    userId: string
}

interface Topic {
    id: string
    title: string
    createdAt: string
    userName: string
}

interface Post {
    id: string
    content: string
    createdAt: string
    userName: string
}

interface PageResponse<T> {
    currentPage: number
    pageSize: number
    totalPages: number
    content: T[]
    last: boolean
    hasNext: boolean
    hasPrevious: boolean
}

export function ForumComponent() {
    const [topics, setTopics] = useState<Topic[]>([])
    const [posts, setPosts] = useState<{ [topicId: string]: Post[] }>({})
    const [currentTopicPage, setCurrentTopicPage] = useState(0)
    const [totalTopicPages, setTotalTopicPages] = useState(0)
    const [postPages, setPostPages] = useState<{ [topicId: string]: number }>({})
    const [totalPostPages, setTotalPostPages] = useState<{ [topicId: string]: number }>({})
    const [expandedTopicId, setExpandedTopicId] = useState<string | null>(null)
    const [isCreateTopicDialogOpen, setIsCreateTopicDialogOpen] = useState(false)
    const [isCreatePostDialogOpen, setIsCreatePostDialogOpen] = useState(false)
    const [newTopicTitle, setNewTopicTitle] = useState('')
    const [newPostContent, setNewPostContent] = useState('')
    const [activeTopicId, setActiveTopicId] = useState<string | null>(null)

    const fetchTopics = useCallback(async (page: number) => {
        try {
            const response = await fetch(`http://localhost:8444/bwp/hhn/api/v1/forum/get-all-topics?pageNo=${page}&pageSize=10`, {
                headers: {
                    'Authorization': `Bearer ${sessionStorage.getItem('jwt_accessToken')}`
                },
            })
            if (response.ok) {
                const data: PageResponse<Topic> = await response.json()
                setTopics(data.content)
                setTotalTopicPages(data.totalPages)
            } else if (response.status === 401 || response.status === 403) {
                window.location.href = '/login'
            } else {
                console.error('Failed to fetch topics:', response.statusText)
            }
        } catch (error) {
            console.error('Error fetching topics:', error)
            toast.error('Failed to load topics. Please try again later.')
        }
    }, [])

    const fetchPosts = useCallback(async (topicId: string, page: number) => {
        try {
            const response = await fetch(`http://localhost:8444/bwp/hhn/api/v1/forum/get-topic-posts?topicId=${topicId}&pageNo=${page}&pageSize=10`, {
                headers: {
                    'Authorization': `Bearer ${sessionStorage.getItem('jwt_accessToken')}`
                },
            })
            if (response.ok) {
                const data: PageResponse<Post> = await response.json()
                setPosts(prev => ({...prev, [topicId]: data.content}))
                setPostPages(prev => ({...prev, [topicId]: data.currentPage}))
                setTotalPostPages(prev => ({...prev, [topicId]: data.totalPages}))
            } else if (response.status === 401 || response.status === 403) {
                window.location.href = '/login'
            } else {
                console.error('Failed to fetch posts:', response.statusText)
            }
        } catch (error) {
            console.error('Error fetching posts:', error)
            toast.error('Failed to load posts. Please try again later.')
        }
    }, [])

    useEffect(() => {
        fetchTopics(currentTopicPage)
    }, [currentTopicPage, fetchTopics])

    const handleCreateTopic = async () => {
        try {

            const jwt = sessionStorage.getItem('jwt_accessToken');

            if (!jwt) {
                toast.error('JWT token not found. Please log in again.')
                return
            }

            const decodedToken = jwtDecode<JwtPayload>(jwt);
            const userId = decodedToken.userId;
            const response = await fetch(`http://localhost:8444/bwp/hhn/api/v1/forum/create-topic?userId=${userId}`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${jwt}`
                },
                body: JSON.stringify({title: newTopicTitle})
            })
            if (response.ok) {
                toast.success('Topic created successfully')
                setIsCreateTopicDialogOpen(false)
                setNewTopicTitle('')
                fetchTopics(currentTopicPage)
            } else {
                toast.error('Failed to create topic')
            }
        } catch (error) {
            console.error('Error creating topic:', error)
            toast.error('An error occurred while creating the topic')
        }
    }

    const handleDeleteTopic = async (topicId: string) => {
        try {
            const jwt = sessionStorage.getItem('jwt_accessToken');

            if (!jwt) {
                toast.error('JWT token not found. Please log in again.')
                return
            }

            const decodedToken = jwtDecode<JwtPayload>(jwt);
            const userId = decodedToken.userId;
            const response = await fetch(`http://localhost:8444/bwp/hhn/api/v1/forum/delete-topic?topicId=${topicId}&userId=${userId}`, {
                method: 'DELETE',
                headers: {
                    'Authorization': `Bearer ${sessionStorage.getItem('jwt_accessToken')}`
                }
            })
            if (response.ok) {
                toast.success('Topic deleted successfully')
                fetchTopics(currentTopicPage)
            } else {
                toast.error('Failed to delete topic')
            }
        } catch (error) {
            console.error('Error deleting topic:', error)
            toast.error('An error occurred while deleting the topic')
        }
    }

    const handleCreatePost = async () => {
        if (!activeTopicId) return
        try {
            const jwt = sessionStorage.getItem('jwt_accessToken');

            if (!jwt) {
                toast.error('JWT token not found. Please log in again.')
                return
            }

            const decodedToken = jwtDecode<JwtPayload>(jwt);
            const userId = decodedToken.userId;
            const response = await fetch(`http://localhost:8444/bwp/hhn/api/v1/forum/create-post?topicId=${activeTopicId}&userId=${userId}`, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${sessionStorage.getItem('jwt_accessToken')}`
                },
                body: JSON.stringify({content: newPostContent})
            })
            if (response.ok) {
                toast.success('Post created successfully')
                setIsCreatePostDialogOpen(false)
                setNewPostContent('')
                fetchPosts(activeTopicId, postPages[activeTopicId] || 0)
            } else {
                toast.error('Failed to create post')
            }
        } catch (error) {
            console.error('Error creating post:', error)
            toast.error('An error occurred while creating the post')
        }
    }

    const handleDeletePost = async (postId: string, topicId: string) => {
        try {
            const jwt = sessionStorage.getItem('jwt_accessToken');

            if (!jwt) {
                toast.error('JWT token not found. Please log in again.')
                return
            }

            const decodedToken = jwtDecode<JwtPayload>(jwt);
            const userId = decodedToken.userId;
            const response = await fetch(`http://localhost:8444/bwp/hhn/api/v1/forum/delete-post?postId=${postId}&userId=${userId}`, {
                method: 'PUT',
                headers: {
                    'Authorization': `Bearer ${sessionStorage.getItem('jwt_accessToken')}`
                }
            })
            if (response.ok) {
                toast.success('Post deleted successfully')
                fetchPosts(topicId, postPages[topicId] || 0)
            } else {
                toast.error('Failed to delete post')
            }
        } catch (error) {
            console.error('Error deleting post:', error)
            toast.error('An error occurred while deleting the post')
        }
    }

    const toggleTopicExpansion = (topicId: string) => {
        if (expandedTopicId === topicId) {
            setExpandedTopicId(null)
        } else {
            setExpandedTopicId(topicId)
            if (!posts[topicId]) {
                fetchPosts(topicId, 0)
            }
        }
    }

    return (
        <Card className="w-full">
            <CardHeader className="flex flex-row items-center justify-between">
                <CardTitle className="text-2xl font-bold flex items-center space-x-2">
                    <MessageSquare className="h-6 w-6 text-primary"/>
                    <span>Forum Mieszkańców</span>
                </CardTitle>
                <Dialog open={isCreateTopicDialogOpen} onOpenChange={setIsCreateTopicDialogOpen}>
                    <DialogTrigger asChild>
                        <Button>
                            <Plus className="mr-2 h-4 w-4"/>
                            Nowy Temat
                        </Button>
                    </DialogTrigger>
                    <DialogContent>
                        <DialogHeader>
                            <DialogTitle>Utwórz Nowy Temat</DialogTitle>
                        </DialogHeader>
                        <Input
                            placeholder="Tytuł tematu"
                            value={newTopicTitle}
                            onChange={(e) => setNewTopicTitle(e.target.value)}
                        />
                        <Button onClick={handleCreateTopic}>Utwórz</Button>
                    </DialogContent>
                </Dialog>
            </CardHeader>
            <CardContent>
                {topics.map((topic) => (
                    <Card key={topic.id} className="mb-4">
                        <CardHeader className="flex flex-row items-center justify-between">
                            <CardTitle>{topic.title}</CardTitle>
                            <div className="flex space-x-2">
                                <Button variant="outline" size="sm" onClick={() => toggleTopicExpansion(topic.id)}>
                                    {expandedTopicId === topic.id ? <ChevronUp className="h-4 w-4"/> :
                                        <ChevronDown className="h-4 w-4"/>}
                                </Button>
                                <Button variant="outline" size="sm" onClick={() => handleDeleteTopic(topic.id)}>
                                    <Trash2 className="h-4 w-4"/>
                                </Button>
                            </div>
                        </CardHeader>
                        <CardContent>
                            <p className="text-sm text-muted-foreground">
                                Utworzono przez: {topic.userName ? topic.userName : 'user was deleted'} |
                                Data: {format(new Date(topic.createdAt), 'dd-MM-yyyy HH:mm:ss')}
                            </p>
                            <Collapsible open={expandedTopicId === topic.id}>
                                <CollapsibleContent>
                                    <ScrollArea className="h-[300px] mt-4">
                                        {posts[topic.id]?.map((post) => (
                                            <Card key={post.id} className="mb-2">
                                                <CardContent className="py-2">
                                                    <div className="flex justify-between items-start">
                                                        <div>
                                                            <p>{post.content}</p>
                                                            <p className="text-sm text-muted-foreground mt-1">
                                                                {post.userName ? post.userName : 'user was deleted'} | {format(new Date(post.createdAt), 'dd-MM-yyyy HH:mm:ss')}
                                                            </p>
                                                        </div>
                                                        <Button variant="outline" size="sm"
                                                                onClick={() => handleDeletePost(post.id, topic.id)}>
                                                            <Trash2 className="h-4 w-4"/>
                                                        </Button>
                                                    </div>
                                                </CardContent>
                                            </Card>
                                        ))}
                                    </ScrollArea>
                                    <div className="flex justify-between mt-2">
                                        <Button
                                            variant="outline"
                                            size="sm"
                                            onClick={() => fetchPosts(topic.id, Math.max(0, (postPages[topic.id] || 0) - 1))}
                                            disabled={!postPages[topic.id] || postPages[topic.id] === 0}
                                        >
                                            <ChevronLeft className="h-4 w-4"/>
                                            Poprzednie
                                        </Button>
                                        <Dialog open={isCreatePostDialogOpen} onOpenChange={setIsCreatePostDialogOpen}>
                                            <DialogTrigger asChild>
                                                <Button size="sm" onClick={() => setActiveTopicId(topic.id)}>
                                                    <Plus className="mr-2 h-4 w-4"/>
                                                    Dodaj Post
                                                </Button>
                                            </DialogTrigger>
                                            <DialogContent>
                                                <DialogHeader>
                                                    <DialogTitle>Dodaj Nowy Post</DialogTitle>
                                                </DialogHeader>
                                                <Textarea
                                                    placeholder="Treść posta"
                                                    value={newPostContent}
                                                    onChange={(e) => setNewPostContent(e.target.value)}
                                                />
                                                <Button onClick={handleCreatePost}>Dodaj</Button>
                                            </DialogContent>
                                        </Dialog>
                                        <Button
                                            variant="outline"
                                            size="sm"
                                            onClick={() => fetchPosts(topic.id, (postPages[topic.id] || 0) + 1)}
                                            disabled={!totalPostPages[topic.id] || (postPages[topic.id] || 0) >= totalPostPages[topic.id] - 1}
                                        >
                                            Następne
                                            <ChevronRight className="h-4 w-4"/>
                                        </Button>
                                    </div>
                                </CollapsibleContent>
                            </Collapsible>
                        </CardContent>
                    </Card>
                ))}
                <div className="flex justify-between items-center mt-4">
                    <Button
                        variant="outline"
                        onClick={() => setCurrentTopicPage(prev => Math.max(prev - 1, 0))}
                        disabled={currentTopicPage === 0}
                    >
                        <ChevronLeft className="mr-2 h-4 w-4"/>
                        Poprzednia
                    </Button>
                    <span>Strona {currentTopicPage + 1} z {totalTopicPages}</span>
                    <Button
                        variant="outline"
                        onClick={() => setCurrentTopicPage(prev => Math.min(prev + 1, totalTopicPages - 1))}
                        disabled={currentTopicPage === totalTopicPages - 1}
                    >
                        Następna
                        <ChevronRight className="ml-2 h-4 w-4"/>
                    </Button>
                </div>
            </CardContent>
        </Card>
    )
}