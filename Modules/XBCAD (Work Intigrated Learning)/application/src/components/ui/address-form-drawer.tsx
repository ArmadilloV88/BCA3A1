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
  FormField,
  FormItem,
  FormLabel,
  FormMessage,
} from '@/components/shadcn-ui/form'

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
import { Textarea } from '../shadcn-ui/textarea'
import { Address } from '@/models/address'

const addressSchema = z.object({
  address: z
    .string()
    .min(5, { message: 'Address must be at least 5 characters long' })
    .max(200, { message: 'Address must not exceed 200 characters' }),
})

interface Props {
  mode?: 'create' | 'update'
  addressToUpdate?: Address
  trigger?: ReactNode
  isOpen?: boolean
  onOpenChanged?: (open: boolean) => void
  itemAction: UseMutationResult<void, Error, Address, unknown>
}

export default function AddressFormDrawer({
  mode = 'create',
  addressToUpdate,
  trigger,
  isOpen,
  onOpenChanged,
  itemAction,
}: Props) {
  const form = useForm<Address>({
    resolver: zodResolver(addressSchema),
    defaultValues:
      mode === 'update' && addressToUpdate
        ? {
            address: addressToUpdate?.address,
          }
        : {
            address: '',
          },
  })

  const onSubmit = (data: Address) => {
    if (mode === 'create') itemAction.mutate(data)
    else if (addressToUpdate) {
      data.id = addressToUpdate?.id
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
            Add Address
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
            {mode === 'create' ? 'Add an' : 'Update'} Address
          </DrawerTitle>
          <DrawerDescription>
            Fill in the following fields to{' '}
            {mode === 'create'
              ? 'add a new address to the application'
              : 'update an address'}
          </DrawerDescription>
        </DrawerHeader>

        <div className="h-full overflow-auto p-5">
          <Form {...form}>
            <form className="space-y-5">
              <FormField
                control={form.control}
                name="address"
                render={({ field }) => (
                  <FormItem>
                    <FormLabel>Address</FormLabel>
                    <FormControl>
                      <Textarea
                        {...field}
                        placeholder="Enter the full address here..."
                        className="resize-none"
                        rows={3}
                      />
                    </FormControl>
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
                  <span>Adding Address...</span>
                </>
              ) : (
                <>
                  <CirclePlus className="mr-1 size-4" />
                  <span>Add Address</span>
                </>
              )
            ) : itemAction.isPending ? (
              <>
                <Loader2 className="mr-2 h-4 w-4 animate-spin" />
                <span>Updating Address...</span>
              </>
            ) : (
              <>
                <Pencil className="mr-1 size-4" />
                <span>Update Address</span>
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
