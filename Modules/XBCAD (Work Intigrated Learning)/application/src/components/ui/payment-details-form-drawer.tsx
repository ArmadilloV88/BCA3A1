// The following code was adapted from a v0 staticGenerationAsyncStorage
// Link: https://v0.dev/
'use client'

import { useForm } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import * as z from 'zod'
import { Button } from '@/components/shadcn-ui/button'
import {
  Form,
  FormControl,
  FormDescription,
  FormField,
  FormItem,
  FormLabel,
  FormMessage,
} from '@/components/shadcn-ui/form'
import { Input } from '@/components/shadcn-ui/input'
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/shadcn-ui/select'

import {
  Drawer,
  DrawerTrigger,
  DrawerClose,
  DrawerContent,
  DrawerHeader,
  DrawerFooter,
  DrawerTitle,
  DrawerDescription,
} from '@/components/shadcn-ui/drawer'
import { CirclePlus, Loader2, Pencil } from 'lucide-react'
import { ReactNode } from 'react'
import { UseMutationResult } from '@tanstack/react-query'
import { PaymentDetails } from '@/models/payment-details'

const paymentDetailsSchema = z.object({
  name: z.string().min(2, 'Name must be at least 2 characters long'),
  bank: z.string().min(1, 'Bank name is required'),
  branch: z.string().min(1, 'Branch name is required'),
  branchCode: z
    .number()
    .int()
    .positive('Branch code must be a positive integer')
    .max(99999999, "Branch code can't be more than 8 digits long"),
  accountNumber: z
    .number()
    .int()
    .positive('Account number must be a positive integer')
    .max(99999999999999999, "Account number can't be more than 17 digits long"),
})

interface Props {
  mode?: 'create' | 'update'
  paymentDetailsToUpdate?: PaymentDetails
  trigger?: ReactNode
  isOpen?: boolean
  onOpenChanged?: (open: boolean) => void
  itemAction: UseMutationResult<void, Error, PaymentDetails, unknown>
}

export default function PaymentDetailsFormDrawer({
  mode = 'create',
  paymentDetailsToUpdate,
  trigger,
  isOpen,
  onOpenChanged,
  itemAction,
}: Props) {
  const form = useForm<PaymentDetails>({
    resolver: zodResolver(paymentDetailsSchema),
    defaultValues:
      mode === 'update' && paymentDetailsToUpdate
        ? {
            name: paymentDetailsToUpdate?.name,
            bank: paymentDetailsToUpdate?.bank,
            branch: paymentDetailsToUpdate?.branch,
            branchCode: paymentDetailsToUpdate?.branchCode,
            accountNumber: paymentDetailsToUpdate?.accountNumber,
          }
        : {
            name: '',
            bank: '',
            branch: '',
            branchCode: 0,
            accountNumber: 0,
          },
  })

  const onSubmit = (data: PaymentDetails) => {
    if (mode === 'create') itemAction.mutate(data)
    else if (paymentDetailsToUpdate) {
      data.id = paymentDetailsToUpdate?.id
      itemAction.mutate(data)
    }

    form.reset()

    if (itemAction.isIdle && !itemAction.isError && onOpenChanged) {
      onOpenChanged(false)
    }
  }

  return (
    <Drawer direction="right" open={isOpen} onOpenChange={onOpenChanged}>
      <DrawerTrigger asChild>
        {trigger ? (
          trigger
        ) : (
          <Button size="sm" className="hidden h-8 lg:flex">
            <CirclePlus className="mr-1 size-4" />
            Add Payment Details
          </Button>
        )}
      </DrawerTrigger>
      <DrawerContent
        className="fixed inset-x-auto bottom-2 right-2 top-2 mt-0 flex w-[350px] rounded-md outline-none after:!content-none"
        style={
          {
            '--initial-transform': 'calc(100% + 8px)',
          } as React.CSSProperties
        }
      >
        <DrawerHeader>
          <DrawerTitle>
            {mode === 'create' ? 'Add' : 'Update'} Payment Details
          </DrawerTitle>
          <DrawerDescription>
            Fill in the following fields to{' '}
            {mode === 'create'
              ? 'add new payment details to the application'
              : 'update payment details'}
          </DrawerDescription>
        </DrawerHeader>

        <div className="h-full overflow-auto p-5">
          <Form {...form}>
            <form className="space-y-5">
              <FormField
                control={form.control}
                name="name"
                render={({ field }) => (
                  <FormItem>
                    <FormLabel>Name</FormLabel>
                    <FormControl>
                      <Input {...field} />
                    </FormControl>
                    <FormDescription>
                      The name associated with this payment detail.
                    </FormDescription>
                    <FormMessage />
                  </FormItem>
                )}
              />
              <FormField
                control={form.control}
                name="bank"
                render={({ field }) => (
                  <FormItem>
                    <FormLabel>Bank</FormLabel>
                    <FormControl>
                      <Input {...field} />
                    </FormControl>
                    <FormDescription>The name of the bank.</FormDescription>
                    <FormMessage />
                  </FormItem>
                )}
              />
              <FormField
                control={form.control}
                name="branch"
                render={({ field }) => (
                  <FormItem>
                    <FormLabel>Branch</FormLabel>
                    <FormControl>
                      <Input {...field} />
                    </FormControl>
                    <FormDescription>
                      The name of the bank branch.
                    </FormDescription>
                    <FormMessage />
                  </FormItem>
                )}
              />
              <FormField
                control={form.control}
                name="branchCode"
                render={({ field }) => (
                  <FormItem>
                    <FormLabel>Branch Code</FormLabel>
                    <FormControl>
                      <Input
                        type="number"
                        {...field}
                        onChange={(e) =>
                          field.onChange(parseInt(e.target.value))
                        }
                      />
                    </FormControl>
                    <FormDescription>
                      The code for this bank branch.
                    </FormDescription>
                    <FormMessage />
                  </FormItem>
                )}
              />
              <FormField
                control={form.control}
                name="accountNumber"
                render={({ field }) => (
                  <FormItem>
                    <FormLabel>Account Number</FormLabel>
                    <FormControl>
                      <Input
                        type="number"
                        {...field}
                        onChange={(e) =>
                          field.onChange(parseInt(e.target.value))
                        }
                      />
                    </FormControl>
                    <FormDescription>
                      The account number for this payment detail.
                    </FormDescription>
                    <FormMessage />
                  </FormItem>
                )}
              />
            </form>
          </Form>
        </div>

        <DrawerFooter>
          <Button
            onClick={form.handleSubmit(onSubmit)}
            disabled={itemAction.isPending}
          >
            {mode === 'create' ? (
              itemAction.isPending ? (
                <>
                  <Loader2 className="mr-2 h-4 w-4 animate-spin" />
                  <span>Adding Payment Details...</span>
                </>
              ) : (
                <>
                  <CirclePlus className="mr-1 size-4" />
                  <span>Add Payment Details</span>
                </>
              )
            ) : itemAction.isPending ? (
              <>
                <Loader2 className="mr-2 h-4 w-4 animate-spin" />
                <span>Updating Payment Details...</span>
              </>
            ) : (
              <>
                <Pencil className="mr-1 size-4" />
                <span>Update Payment Details</span>
              </>
            )}
          </Button>
          <DrawerClose asChild>
            <Button variant="outline">Cancel</Button>
          </DrawerClose>
        </DrawerFooter>
      </DrawerContent>
    </Drawer>
  )
}
