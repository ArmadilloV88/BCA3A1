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
import { Textarea } from '../shadcn-ui/textarea'
import { Horse } from '@/models/horse'
import useSkillLevels from '@/hooks/useSkillLevels'

const horseSchema = z.object({
  name: z
    .string()
    .min(2, { message: 'Name must be at least 2 characters long' })
    .max(50, { message: 'Name must not exceed 50 characters' }),
  age: z
    .number()
    .int()
    .positive({ message: 'Age must be a positive integer' })
    .max(40, { message: 'Age must not exceed 40 years' }),
  breed: z
    .string()
    .min(2, { message: 'Breed must be at least 2 characters long' })
    .max(50, { message: 'Breed must not exceed 50 characters' }),
  yearsWorked: z
    .number()
    .int()
    .min(0, { message: 'Years worked must be 0 or more' })
    .max(30, { message: 'Years worked must not exceed 30' }),
  skillLevelId: z.number().int().nullable().optional(),
})

interface Props {
  mode?: 'create' | 'update'
  horseToUpdate?: Horse
  trigger?: ReactNode
  isOpen?: boolean
  onOpenChanged?: (open: boolean) => void
  itemAction: UseMutationResult<void, Error, Horse, unknown>
}

export default function HorseFormDrawer({
  mode = 'create',
  horseToUpdate,
  trigger,
  isOpen,
  onOpenChanged,
  itemAction,
}: Props) {
  const { skillLevels } = useSkillLevels()
  const form = useForm<Horse>({
    resolver: zodResolver(horseSchema),
    defaultValues:
      mode === 'update' && horseToUpdate
        ? {
            name: horseToUpdate?.name,
            age: horseToUpdate?.age,
            breed: horseToUpdate?.breed,
            yearsWorked: horseToUpdate?.yearsWorked,
            skillLevelId: horseToUpdate?.skillLevel?.id,
          }
        : {
            name: '',
            age: 0,
            breed: '',
            yearsWorked: 0,
            skillLevel: undefined,
          },
  })

  const onSubmit = (data: Horse) => {
    // NEED TO ADD CREATED BY!

    console.log(data)
    if (mode === 'create') itemAction.mutate(data)
    else if (horseToUpdate) {
      data.id = horseToUpdate?.id
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
            Add Horse
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
            {mode === 'create' ? 'Add a' : 'Update'} Horse
          </DrawerTitle>
          <DrawerDescription>
            Fill in the following fields to{' '}
            {mode === 'create'
              ? 'add a new horse to the application'
              : "update a horse's details"}
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
                    <FormMessage />
                  </FormItem>
                )}
              />

              <FormField
                control={form.control}
                name="breed"
                render={({ field }) => (
                  <FormItem>
                    <FormLabel>Breed</FormLabel>
                    <FormControl>
                      <Input {...field} />
                    </FormControl>
                    <FormMessage />
                  </FormItem>
                )}
              />

              <FormField
                control={form.control}
                name="age"
                render={({ field }) => (
                  <FormItem>
                    <FormLabel>Age</FormLabel>
                    <FormControl>
                      <Input
                        type="number"
                        {...field}
                        onChange={(e) =>
                          field.onChange(parseInt(e.target.value))
                        }
                      />
                    </FormControl>
                    <FormMessage />
                  </FormItem>
                )}
              />

              <FormField
                control={form.control}
                name="yearsWorked"
                render={({ field }) => (
                  <FormItem>
                    <FormLabel>Years Worked</FormLabel>
                    <FormControl>
                      <Input
                        type="number"
                        {...field}
                        onChange={(e) =>
                          field.onChange(parseInt(e.target.value))
                        }
                      />
                    </FormControl>
                    <FormMessage />
                  </FormItem>
                )}
              />
              <FormField
                control={form.control}
                name="skillLevelId"
                render={({ field }) => (
                  <FormItem>
                    <FormLabel>Skill Level</FormLabel>
                    <Select
                      onValueChange={(value) => field.onChange(parseInt(value))}
                      defaultValue={field?.value?.toString()}
                    >
                      <FormControl>
                        <SelectTrigger>
                          <SelectValue placeholder="Select a skill level" />
                        </SelectTrigger>
                      </FormControl>
                      <SelectContent>
                        {skillLevels?.data?.map((item) => (
                          <SelectItem key={item.id} value={item.id.toString()}>
                            {item.level}
                          </SelectItem>
                        ))}
                      </SelectContent>
                    </Select>
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
                  <span>Adding Horse...</span>
                </>
              ) : (
                <>
                  <CirclePlus className="mr-1 size-4" />
                  <span>Add Horse</span>
                </>
              )
            ) : itemAction.isPending ? (
              <>
                <Loader2 className="mr-2 h-4 w-4 animate-spin" />
                <span>Updating Horse...</span>
              </>
            ) : (
              <>
                <Pencil className="mr-1 size-4" />
                <span>Update Horse</span>
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
