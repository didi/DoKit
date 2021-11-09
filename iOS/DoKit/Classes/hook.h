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

#ifndef dokit_hook_h
#define dokit_hook_h

#include <stdbool.h>
#include <stddef.h>
#include <stdint.h>

#define DOKIT_HOOK_EXPORT __attribute__((visibility("default")))

#ifdef __cplusplus
#define EXTERN_C_START
extern "C"
{
#define EXTERN_C_END }
#else
#define EXTERN_C_START
#define EXTERN_C_END
#endif

EXTERN_C_START

struct DKRebinding {
    const char *name;
    void *replacement;
    uintptr_t *replaced;
};

DOKIT_HOOK_EXPORT bool DKRebindSymbols(struct DKRebinding rebinding[], size_t length);

EXTERN_C_END

#endif
