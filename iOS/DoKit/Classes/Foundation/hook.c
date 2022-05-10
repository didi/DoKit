/**
 * Copyright 2017 Beijing DiDi Infinity Technology and Development Co., Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

#if !defined(DEBUG) && !defined(NDEBUG)
#define NDEBUG 1
#endif

#include "hook.h"
#include <sys/queue.h>
#include <stdlib.h>
#include <string.h>
#include <mach-o/dyld.h>
#include <mach-o/nlist.h>
#include <assert.h>
#include <mach/vm_types.h>
#include <mach/vm_map.h>
#include <mach/mach_init.h>

static const char *const SEG_DATA_CONST = "__DATA_CONST";

#ifndef NDEBUG
static char *const VM_PROTECT_ERROR = "vm_protect() error.";
#endif

#ifdef __LP64__
typedef struct mach_header_64 mach_header_t;
typedef struct segment_command_64 segment_command_t;
typedef struct section_64 section_t;
typedef struct nlist_64 nlist_t;
#define LC_SEGMENT_ARCH_DEPENDENT LC_SEGMENT_64
#else
typedef struct mach_header mach_header_t;
typedef struct segment_command segment_command_t;
typedef struct section section_t;
typedef struct nlist nlist_t;
#define LC_SEGMENT_ARCH_DEPENDENT LC_SEGMENT
#endif

struct RebindingEntry {
    SLIST_ENTRY(RebindingEntry) node;
    size_t length;
    // [] is correct?
    // or should we use [0]?
    struct DKRebinding rebinding[];
};

static SLIST_HEAD(, RebindingEntry) rebindingEntryHead = SLIST_HEAD_INITIALIZER();

static bool appendRebinding(const struct DKRebinding rebinding[], size_t length) {
    if (!rebinding || !length) {
        assert(false && "appendRebinding() parameter must be non-null.");

        return true;
    }
    struct RebindingEntry *rebindingEntry = malloc(sizeof(struct RebindingEntry) + length * sizeof(struct DKRebinding));
    if (!rebindingEntry) {
        return false;
    }
    rebindingEntry->length = length;
    memmove(rebindingEntry->rebinding, rebinding, length * sizeof(struct DKRebinding));
    SLIST_INSERT_HEAD(&rebindingEntryHead, rebindingEntry, node);

    return true;
}

static void performRebindingWithSection(const section_t *section, uintptr_t slide, const nlist_t *symbolTable, const char *stringTable, const uint32_t *indirectSymbolTable) {
    // section->size could be zero?
    if (!section || !symbolTable || !stringTable || !indirectSymbolTable || !section->size) {
        assert(false && "performRebindingWithSection() parameter error.");

        return;
    }
    uint32_t *sectionIndirectSymbolTable = (uint32_t *) (indirectSymbolTable + section->reserved1);
    // section->addr is vm address
    uintptr_t *got = (uintptr_t *) (slide + section->addr);

    // Get got located vm region basic info
    vm_address_t vmAddress = (vm_address_t) got;
    vm_size_t vmSize = 0;
    memory_object_t memoryObject;
#ifdef __LP64__
    mach_msg_type_number_t machMsgTypeNumber = VM_REGION_BASIC_INFO_COUNT_64;
    vm_region_basic_info_data_64_t vmRegionBasicInfoData;
    kern_return_t kernReturn = vm_region_64(mach_task_self(), &vmAddress, &vmSize, VM_REGION_BASIC_INFO_64, (vm_region_info_t) &vmRegionBasicInfoData, &machMsgTypeNumber, &memoryObject);
#else
    mach_msg_type_number_t machMsgTypeNumber = VM_REGION_BASIC_INFO_COUNT;
    vm_region_basic_info_data_t vmRegionBasicInfoData;
    kern_return_t kernReturn = vm_region(mach_task_self(), &vmAddress, &vmSize, VM_REGION_BASIC_INFO, (vm_region_info_t) &vmRegionBasicInfoData, &machMsgTypeNumber, &memoryObject);
#endif
    assert(vmAddress <= (vm_address_t) got && vmSize >= section->size && "vmAddress <= got < got + size <= vmAddress + vmSize.");
    if (kernReturn != KERN_SUCCESS) {
        assert(false && "vm_region_64() error.");

        return;
    }
    vm_prot_t newProtection = VM_PROT_NONE;
    if (vmRegionBasicInfoData.max_protection == VM_PROT_READ) {
        assert(vmRegionBasicInfoData.protection == VM_PROT_READ);
        newProtection = VM_PROT_READ | VM_PROT_WRITE | VM_PROT_COPY;
    } else if (vmRegionBasicInfoData.protection == VM_PROT_READ) {
        newProtection = VM_PROT_READ | VM_PROT_WRITE;
    }
    if (newProtection != VM_PROT_NONE) {
        // got and section->size will be Page-Aligned
        kernReturn = vm_protect(mach_task_self(), (vm_address_t) got, section->size, false, newProtection);
        if (kernReturn != KERN_SUCCESS) {
            assert(false && VM_PROTECT_ERROR);

            return;
        }
    }

    for (uint64_t i = 0; i < section->size / sizeof(uintptr_t); ++i) {
        uint32_t symbolTableIndex = sectionIndirectSymbolTable[i];
        if (symbolTableIndex == INDIRECT_SYMBOL_ABS || symbolTableIndex == INDIRECT_SYMBOL_LOCAL || symbolTableIndex == (INDIRECT_SYMBOL_LOCAL | INDIRECT_SYMBOL_ABS)) {
            continue;
        }
        uint32_t stringTableOffset = symbolTable[symbolTableIndex].n_un.n_strx;
        const char *symbolName = stringTable + stringTableOffset;
        // "\0" || "_\0" is invalid symbol name
        bool symbolNameLongerThanOneCharacter = symbolName[0] && symbolName[1];
        struct RebindingEntry *rebindingEntry;
        SLIST_FOREACH(rebindingEntry, &rebindingEntryHead, node) {
            for (size_t j = 0; j < rebindingEntry->length; ++j) {
                // strcmp -> strncmp
                if (symbolNameLongerThanOneCharacter && strcmp(&symbolName[1], rebindingEntry->rebinding[j].name) == 0) {
                    if (rebindingEntry->rebinding[j].replaced && got[i] != (uintptr_t) rebindingEntry->rebinding[j].replacement) {
                        *(rebindingEntry->rebinding[j].replaced) = got[i];
                    }

                    got[i] = (uintptr_t) rebindingEntry->rebinding[j].replacement;

                    break;
                }
            }
        }
    }

    if (vmRegionBasicInfoData.protection == VM_PROT_READ) {
        kernReturn = vm_protect(mach_task_self(), (vm_address_t) got, section->size, false, vmRegionBasicInfoData.protection);
        if (kernReturn != KERN_SUCCESS) {
            assert(false && VM_PROTECT_ERROR);

            return;
        }
    }
}

static void dyldAddImageCallback(const struct mach_header *machHeader, intptr_t vmAddrSlide) {
    // Skip Mach-O file header
    uintptr_t currentPointer = (uintptr_t) machHeader + sizeof(mach_header_t);

    segment_command_t *linkEditCommand = NULL;
    struct symtab_command *symbolTableCommand = NULL;
    struct dysymtab_command *dynamicSymbolTableCommand = NULL;

    // Iterator
    segment_command_t *currentSegmentCommand;

    for (uint32_t i = 0; i < machHeader->ncmds; ++i, currentPointer += currentSegmentCommand->cmdsize) {
        currentSegmentCommand = (segment_command_t *) currentPointer;
        if (currentSegmentCommand->cmd == LC_SEGMENT_ARCH_DEPENDENT) {
            if (strcmp(currentSegmentCommand->segname, SEG_LINKEDIT) == 0) {
                linkEditCommand = currentSegmentCommand;
            }
        } else if (currentSegmentCommand->cmd == LC_SYMTAB) {
            symbolTableCommand = (struct symtab_command *) currentSegmentCommand;
        } else if (currentSegmentCommand->cmd == LC_DYSYMTAB) {
            dynamicSymbolTableCommand = (struct dysymtab_command *) currentSegmentCommand;
        }
    }
    if (!symbolTableCommand || !dynamicSymbolTableCommand || !linkEditCommand) {
        assert(false && "SYMBOL or DYNAMIC_SYMBOL or LINK_EDIT error.");

        return;
    } else if (!dynamicSymbolTableCommand->nindirectsyms) {
        // UIKit will no indirect symbol call
        return;
    }
    // Find read-only base address
    uintptr_t linkEditBasePointer = vmAddrSlide + linkEditCommand->vmaddr - linkEditCommand->fileoff;
    nlist_t *symbolTable = (nlist_t *) (linkEditBasePointer + symbolTableCommand->symoff);
    char *stringTable = (char *) (linkEditBasePointer + symbolTableCommand->stroff);
    uint32_t *indirectSymbolTable = (uint32_t *) (linkEditBasePointer + dynamicSymbolTableCommand->indirectsymoff);

    currentPointer = (uintptr_t) machHeader + sizeof(mach_header_t);
    for (uint32_t i = 0; i < machHeader->ncmds; ++i, currentPointer += currentSegmentCommand->cmdsize) {
        currentSegmentCommand = (segment_command_t *) currentPointer;
        if (currentSegmentCommand->cmd == LC_SEGMENT_ARCH_DEPENDENT) {
            if (strncmp(currentSegmentCommand->segname, SEG_DATA, 6) != 0 && strncmp(currentSegmentCommand->segname, SEG_DATA_CONST, 12)) {
                continue;
            }
            for (uint32_t j = 0; j < currentSegmentCommand->nsects; ++j) {
                // section = sectionBase[j]
                section_t *section = ((section_t *) (currentPointer + sizeof(segment_command_t))) + j;
                uint32_t sectionType = section->flags & SECTION_TYPE;
                if (sectionType == S_LAZY_SYMBOL_POINTERS || sectionType == S_NON_LAZY_SYMBOL_POINTERS) {
                    performRebindingWithSection(section, (uintptr_t) vmAddrSlide, symbolTable, stringTable, indirectSymbolTable);
                }
            }
        }
    }
}

bool DKRebindSymbols(struct DKRebinding rebinding[], size_t length) {
    bool retVal = appendRebinding(rebinding, length);
    if (!retVal) {
        return false;
    }
    if (SLIST_NEXT(SLIST_FIRST(&rebindingEntryHead), node)) {
        uint32_t count = _dyld_image_count();
        for (uint32_t i = 0; i < count; ++i) {
            dyldAddImageCallback(_dyld_get_image_header(i), _dyld_get_image_vmaddr_slide(i));
        }
    } else {
        // if this is the first call, register the callback for image addition (which is also invoked for existing images)
        _dyld_register_func_for_add_image(dyldAddImageCallback);
    }

    return retVal;
}


