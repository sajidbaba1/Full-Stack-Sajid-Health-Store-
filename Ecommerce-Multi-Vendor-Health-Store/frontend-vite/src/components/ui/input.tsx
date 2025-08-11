import * as React from "react"
import { cva, type VariantProps } from "class-variance-authority"
import { motion } from "framer-motion"
import { cn } from "@/lib/utils"

const inputVariants = cva(
  "flex w-full rounded-md border border-input bg-background px-3 py-2 text-sm ring-offset-background file:border-0 file:bg-transparent file:text-sm file:font-medium placeholder:text-muted-foreground focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2 disabled:cursor-not-allowed disabled:opacity-50 transition-all duration-200",
  {
    variants: {
      variant: {
        default: "border-input focus-visible:ring-ring",
        destructive: "border-destructive focus-visible:ring-destructive",
        success: "border-green-500 focus-visible:ring-green-500",
        ghost: "border-transparent bg-transparent hover:bg-accent focus-visible:bg-background",
      },
      size: {
        default: "h-10 px-3 py-2",
        sm: "h-9 px-3 py-2 text-sm",
        lg: "h-11 px-4 py-2",
      },
    },
    defaultVariants: {
      variant: "default",
      size: "default",
    },
  }
)

type InputVariantProps = VariantProps<typeof inputVariants>;

type InputSize = InputVariantProps['size'];
type InputVariant = InputVariantProps['variant'];

export interface InputProps
  extends Omit<React.InputHTMLAttributes<HTMLInputElement>, 'size' | 'variant'> {
  /** Size variant of the input */
  size?: InputSize;
  /** Visual variant of the input */
  variant?: InputVariant;
  /** Icon to display on the left side of the input */
  leftIcon?: React.ReactNode;
  /** Icon to display on the right side of the input */
  rightIcon?: React.ReactNode;
  /** Error message to display below the input */
  error?: string;
  /** Success message to display below the input */
  success?: string;
  /** Label text for the input */
  label?: string;
  /** Helper text to display below the input */
  helperText?: string;
}

const Input = React.forwardRef<HTMLInputElement, InputProps>(
  ({ 
    className, 
    variant, 
    size, 
    type, 
    leftIcon, 
    rightIcon, 
    error, 
    success, 
    label, 
    helperText,
    id,
    ...props 
  }, ref) => {
    const inputId = id || React.useId()
    const hasError = !!error
    const hasSuccess = !!success && !hasError
    
    const effectiveVariant = hasError ? "destructive" : hasSuccess ? "success" : variant

    return (
      <div className="space-y-2">
        {label && (
          <label 
            htmlFor={inputId}
            className="text-sm font-medium leading-none peer-disabled:cursor-not-allowed peer-disabled:opacity-70"
          >
            {label}
          </label>
        )}
        
        <div className="relative">
          {leftIcon && (
            <div className="absolute left-3 top-1/2 -translate-y-1/2 text-muted-foreground">
              {leftIcon}
            </div>
          )}
          
          <input
            type={type}
            className={cn(
              inputVariants({ variant: effectiveVariant, size, className }),
              leftIcon && "pl-10",
              rightIcon && "pr-10"
            )}
            ref={ref}
            id={inputId}
            {...props}
          />
          
          {rightIcon && (
            <div className="absolute right-3 top-1/2 -translate-y-1/2 text-muted-foreground">
              {rightIcon}
            </div>
          )}
        </div>
        
        {(error || success || helperText) && (
          <motion.div
            initial={{ opacity: 0, y: -10 }}
            animate={{ opacity: 1, y: 0 }}
            exit={{ opacity: 0, y: -10 }}
            className="text-sm"
          >
            {error && (
              <p className="text-destructive flex items-center gap-1">
                <svg className="h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 8v4m0 4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
                </svg>
                {error}
              </p>
            )}
            {success && !error && (
              <p className="text-green-600 flex items-center gap-1">
                <svg className="h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M5 13l4 4L19 7" />
                </svg>
                {success}
              </p>
            )}
            {helperText && !error && !success && (
              <p className="text-muted-foreground">{helperText}</p>
            )}
          </motion.div>
        )}
      </div>
    )
  }
)
Input.displayName = "Input"

export { Input, inputVariants }
