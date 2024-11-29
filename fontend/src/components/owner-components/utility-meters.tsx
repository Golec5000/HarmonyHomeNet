'use client'

import React, {useEffect, useState} from 'react'
import {Card, CardContent, CardHeader, CardTitle} from "@/components/ui/card"
import {Droplet, Gauge} from 'lucide-react'
import {toast} from 'sonner'

interface UtilityMeterProps {
    apartmentId: string | null
}

export function UtilityMeters({apartmentId}: UtilityMeterProps) {
    const [waterMeterValue, setWaterMeterValue] = useState<string | null>(null)
    const [electricityMeterValue, setElectricityMeterValue] = useState<string | null>(null)

    useEffect(() => {
        if (apartmentId) {
            fetchMeterValues(apartmentId)
        } else {
            setWaterMeterValue(null)
            setElectricityMeterValue(null)
        }
    }, [apartmentId])

    const fetchMeterValues = async (id: string) => {
        try {
            const waterResponse = await fetch(`http://localhost:8444/bwp/hhn/api/v1/user/utility-meter/water-meter/${id}`, {
                headers: {
                    'Authorization': `Bearer ${sessionStorage.getItem('jwt_accessToken')}`
                }
            })
            const electricityResponse = await fetch(`http://localhost:8444/bwp/hhn/api/v1/user/utility-meter/electricity-meter/${id}`, {
                headers: {
                    'Authorization': `Bearer ${sessionStorage.getItem('jwt_accessToken')}`
                }
            })

            if (waterResponse.ok && electricityResponse.ok) {
                const waterData = await waterResponse.text()
                const electricityData = await electricityResponse.text()
                setWaterMeterValue(waterData)
                setElectricityMeterValue(electricityData)
            } else {
                toast.error('Failed to fetch meter values')
            }
        } catch (error) {
            console.error('Error fetching meter values:', error)
            toast.error('An error occurred while fetching meter values')
        }
    }

    return (
        <div className="space-y-6">
            <div className="text-red-500 font-bold mb-4">
                UWAGA: Wartości liczników są symulowane i nie odzwierciedlają rzeczywistych wartości
            </div>
            <Card>
                <CardHeader>
                    <CardTitle className="flex items-center">
                        <Gauge className="mr-2 h-6 w-6"/>
                        Liczniki
                    </CardTitle>
                </CardHeader>
                <CardContent>
                    {apartmentId ? (
                        <div className="space-y-4">
                            <div className="flex items-center justify-between">
                                <div className="flex items-center">
                                    <Droplet className="mr-2 h-5 w-5 text-blue-500"/>
                                    <span>Licznik wody:</span>
                                </div>
                                <span className="font-bold">{waterMeterValue ?? 'N/A'} m³</span>
                            </div>
                            <div className="flex items-center justify-between">
                                <div className="flex items-center">
                                    <Gauge className="mr-2 h-5 w-5 text-yellow-500"/>
                                    <span>Licznik prądu:</span>
                                </div>
                                <span className="font-bold">{electricityMeterValue ?? 'N/A'} kWh</span>
                            </div>
                        </div>
                    ) : (
                        <div className="text-center text-muted-foreground">
                            Wybierz mieszkanie, aby zobaczyć liczniki
                        </div>
                    )}
                </CardContent>
            </Card>
        </div>
    )
}