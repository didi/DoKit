//
//  doraemon_fishhook.h
//  DoraemonKit-DoraemonKit
//
//  Created by didi on 2020/3/18.
//

#ifndef doraemon_fishhook_h
#define doraemon_fishhook_h

#include <stddef.h>
#include <stdint.h>

#if !defined(DORAEMON_FISHHOOK_EXPORT)
#define DORAEMON_FISHHOOK_VISIBILITY __attribute__((visibility("hidden")))
#else
#define DORAEMON_FISHHOOK_VISIBILITY __attribute__((visibility("default")))
#endif

#ifdef __cplusplus
extern "C" {
#endif //__cplusplus

/*
 * A structure representing a particular intended rebinding from a symbol
 * name to its replacement
 */
struct doraemon_rebinding {
  const char *name;
  void *replacement;
  void **replaced;
};

/*
 * For each rebinding in rebindings, rebinds references to external, indirect
 * symbols with the specified name to instead point at replacement for each
 * image in the calling process as well as for all future images that are loaded
 * by the process. If rebind_functions is called more than once, the symbols to
 * rebind are added to the existing list of rebindingdoraemon_rebind_symbolsl
 * is rebound more than once, the later rebinding will take precedence.
 */
DORAEMON_FISHHOOK_VISIBILITY
int doraemon_rebind_symbols(struct doraemon_rebinding rebindings[], size_t rebindings_nel);

/*
 * Rebinds as above, but only in the specified image. The header should point
 * to the mach-o header, the slide should be the slide offset. Others as above.
 */
DORAEMON_FISHHOOK_VISIBILITY
int doraemon_rebind_symbols_image(void *header,
                         intptr_t slide,
                         struct doraemon_rebinding rebindings[],
                         size_t rebindings_nel);
#ifdef __cplusplus
}
#endif //__cplusplus


#endif /* doraemon_fishhook_h */
