import {AlertCircle} from "lucide-react"
import {Alert, AlertDescription, AlertTitle} from "@/components/ui/alert"

export function Alert404(){
    return (
        <Alert variant="destructive">
            <AlertCircle className="h-4 w-4"/>
            <AlertTitle>Error</AlertTitle>
            <AlertDescription>
                The page you are looking for does not exist.
            </AlertDescription>
        </Alert>
    )
}