/*
 * Author: Landon Fuller <landonf@plausible.coop>
 *
 * Copyright (c) 2012-2013 Plausible Labs Cooperative, Inc.
 * All rights reserved.
 *
 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use,
 * copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following
 * conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 */

/*
 * For external library integrators:
 *
 * Set this value to any valid C symbol prefix. This will automatically
 * prepend the given prefix to all external symbols in the library.
 *
 * This may be used to avoid symbol conflicts between multiple libraries
 * that may both incorporate PLCrashReporter.
 */
// #define PLCRASHREPORTER_PREFIX AcmeCo


// We need two extra layers of indirection to make CPP substitute
// the PLCRASHREPORTER_PREFIX define.
#define PLNS_impl2(prefix, symbol) prefix ## symbol
#define PLNS_impl(prefix, symbol) PLNS_impl2(prefix, symbol)
#define PLNS(symbol) PLNS_impl(PLCRASHREPORTER_PREFIX, symbol)


/*
 * Rewrite all ObjC/C symbols.
 *
 * For C++ symbol handling, refer to the PLCR_CPP_BEGIN_NS and PLCR_CPP_END_NS
 * macros.
 */
#ifdef PLCRASHREPORTER_PREFIX

/* Objective-C Classes */
#define PLCrashMachExceptionServer          PLNS(PLCrashMachExceptionServer)
#define PLCrashReport                       PLNS(PLCrashReport)
#define PLCrashReportApplicationInfo        PLNS(PLCrashReportApplicationInfo)
#define PLCrashReportBinaryImageInfo        PLNS(PLCrashReportBinaryImageInfo)
#define PLCrashReportExceptionInfo          PLNS(PLCrashReportExceptionInfo)
#define PLCrashReportMachExceptionInfo      PLNS(PLCrashReportMachExceptionInfo)
#define PLCrashReportMachineInfo            PLNS(PLCrashReportMachineInfo)
#define PLCrashReportProcessInfo            PLNS(PLCrashReportProcessInfo)
#define PLCrashReportProcessorInfo          PLNS(PLCrashReportProcessorInfo)
#define PLCrashReportRegisterInfo           PLNS(PLCrashReportRegisterInfo)
#define PLCrashReportSignalInfo             PLNS(PLCrashReportSignalInfo)
#define PLCrashReportStackFrameInfo         PLNS(PLCrashReportStackFrameInfo)
#define PLCrashReportSymbolInfo             PLNS(PLCrashReportSymbolInfo)
#define PLCrashReportSystemInfo             PLNS(PLCrashReportSystemInfo)
#define PLCrashReportTextFormatter          PLNS(PLCrashReportTextFormatter)
#define PLCrashReportThreadInfo             PLNS(PLCrashReportThreadInfo)
#define PLCrashReporter                     PLNS(PLCrashReporter)
#define PLCrashSignalHandler                PLNS(PLCrashSignalHandler)
#define PLCrashHostInfo                     PLNS(PLCrashHostInfo)
#define PLCrashMachExceptionPort            PLNS(PLCrashMachExceptionPort)
#define PLCrashMachExceptionPortSet         PLNS(PLCrashMachExceptionPortSet)
#define PLCrashProcessInfo                  PLNS(PLCrashProcessInfo)
#define PLCrashReporterConfig               PLNS(PLCrashReporterConfig)
#define PLCrashUncaughtExceptionHandler     PLNS(PLCrashUncaughtExceptionHandler)
#define PLCrashReportFormatter              PLNS(PLCrashReportFormatter)

/* Public C functions */
#define PLCrashMachExceptionForward         PLNS(PLCrashMachExceptionForward)
#define PLCrashSignalHandlerForward         PLNS(PLCrashSignalHandlerForward)
#define plcrash_signal_handler              PLNS(plcrash_signal_handler)


/* Public C global symbols */
#define PLCrashReporterErrorDomain          PLNS(PLCrashReporterErrorDomain)
#define PLCrashReportHostArchitecture       PLNS(PLCrashReportHostArchitecture)
#define PLCrashReportHostOperatingSystem    PLNS(PLCrashReportHostOperatingSystem)
#define PLCrashReporterException            PLNS(PLCrashReporterException)

/* For backwards compatibility, plcrash_async_byteorder vends C++ methods when included under C++. We have
 * to handle this distinctly from our PLCR_CPP_BEGIN_NS C++ namespacing mechanism. */
#define plcrash_async_byteorder             PLNS(plcrash_async_byteorder)

/*
 * All private C symbols. Once these are migrated to C++, we'll be able to use the much simpler
 * PLCR_CPP_BEGIN_NS machinery.
 *
 * This list was automatically generated (and can be updated) by setting PLCRASHREPORTER_PREFIX to 'AcmeCo',
 * building the library, and executing the following:
 * nm -g -U <static library> | grep '^[0-9]' | c++filt | grep -v AcmeCo | grep -E '_pl|_PL' | awk '{print $3}' | cut -c 2- | sort | uniq | awk '{print "#define",$1,"PLNS("$1")"}'
 */
#define PLCRASH_ASYNC_OBJC_ISA_NONPTR_CLASS_MASK PLNS(PLCRASH_ASYNC_OBJC_ISA_NONPTR_CLASS_MASK)
#define pl_mach_thread_self PLNS(pl_mach_thread_self)
#define plcrash__architecture__descriptor PLNS(plcrash__architecture__descriptor)
#define plcrash__architecture__enum_values_by_name PLNS(plcrash__architecture__enum_values_by_name)
#define plcrash__architecture__enum_values_by_number PLNS(plcrash__architecture__enum_values_by_number)
#define plcrash__crash_report__application_info__descriptor PLNS(plcrash__crash_report__application_info__descriptor)
#define plcrash__crash_report__binary_image__descriptor PLNS(plcrash__crash_report__binary_image__descriptor)
#define plcrash__crash_report__descriptor PLNS(plcrash__crash_report__descriptor)
#define plcrash__crash_report__exception__descriptor PLNS(plcrash__crash_report__exception__descriptor)
#define plcrash__crash_report__free_unpacked PLNS(plcrash__crash_report__free_unpacked)
#define plcrash__crash_report__get_packed_size PLNS(plcrash__crash_report__get_packed_size)
#define plcrash__crash_report__init PLNS(plcrash__crash_report__init)
#define plcrash__crash_report__machine_info__descriptor PLNS(plcrash__crash_report__machine_info__descriptor)
#define plcrash__crash_report__pack PLNS(plcrash__crash_report__pack)
#define plcrash__crash_report__pack_to_buffer PLNS(plcrash__crash_report__pack_to_buffer)
#define plcrash__crash_report__process_info__descriptor PLNS(plcrash__crash_report__process_info__descriptor)
#define plcrash__crash_report__processor__descriptor PLNS(plcrash__crash_report__processor__descriptor)
#define plcrash__crash_report__processor__type_encoding__descriptor PLNS(plcrash__crash_report__processor__type_encoding__descriptor)
#define plcrash__crash_report__processor__type_encoding__enum_values_by_name PLNS(plcrash__crash_report__processor__type_encoding__enum_values_by_name)
#define plcrash__crash_report__processor__type_encoding__enum_values_by_number PLNS(plcrash__crash_report__processor__type_encoding__enum_values_by_number)
#define plcrash__crash_report__report_info__descriptor PLNS(plcrash__crash_report__report_info__descriptor)
#define plcrash__crash_report__signal__descriptor PLNS(plcrash__crash_report__signal__descriptor)
#define plcrash__crash_report__signal__mach_exception__descriptor PLNS(plcrash__crash_report__signal__mach_exception__descriptor)
#define plcrash__crash_report__symbol__descriptor PLNS(plcrash__crash_report__symbol__descriptor)
#define plcrash__crash_report__system_info__descriptor PLNS(plcrash__crash_report__system_info__descriptor)
#define plcrash__crash_report__system_info__operating_system__descriptor PLNS(plcrash__crash_report__system_info__operating_system__descriptor)
#define plcrash__crash_report__system_info__operating_system__enum_values_by_name PLNS(plcrash__crash_report__system_info__operating_system__enum_values_by_name)
#define plcrash__crash_report__system_info__operating_system__enum_values_by_number PLNS(plcrash__crash_report__system_info__operating_system__enum_values_by_number)
#define plcrash__crash_report__thread__descriptor PLNS(plcrash__crash_report__thread__descriptor)
#define plcrash__crash_report__thread__register_value__descriptor PLNS(plcrash__crash_report__thread__register_value__descriptor)
#define plcrash__crash_report__thread__stack_frame__descriptor PLNS(plcrash__crash_report__thread__stack_frame__descriptor)
#define plcrash__crash_report__unpack PLNS(plcrash__crash_report__unpack)
#define plcrash_async_address_apply_offset PLNS(plcrash_async_address_apply_offset)
#define plcrash_async_allocator_alloc PLNS(plcrash_async_allocator_alloc)
#define plcrash_async_allocator_new PLNS(plcrash_async_allocator_new)
#define plcrash_async_byteorder_big_endian PLNS(plcrash_async_byteorder_big_endian)
#define plcrash_async_byteorder_direct PLNS(plcrash_async_byteorder_direct)
#define plcrash_async_byteorder_little_endian PLNS(plcrash_async_byteorder_little_endian)
#define plcrash_async_byteorder_swapped PLNS(plcrash_async_byteorder_swapped)
#define plcrash_async_cfe_entry_apply PLNS(plcrash_async_cfe_entry_apply)
#define plcrash_async_cfe_entry_free PLNS(plcrash_async_cfe_entry_free)
#define plcrash_async_cfe_entry_init PLNS(plcrash_async_cfe_entry_init)
#define plcrash_async_cfe_entry_register_count PLNS(plcrash_async_cfe_entry_register_count)
#define plcrash_async_cfe_entry_register_list PLNS(plcrash_async_cfe_entry_register_list)
#define plcrash_async_cfe_entry_return_address_register PLNS(plcrash_async_cfe_entry_return_address_register)
#define plcrash_async_cfe_entry_stack_adjustment PLNS(plcrash_async_cfe_entry_stack_adjustment)
#define plcrash_async_cfe_entry_stack_offset PLNS(plcrash_async_cfe_entry_stack_offset)
#define plcrash_async_cfe_entry_type PLNS(plcrash_async_cfe_entry_type)
#define plcrash_async_cfe_reader_find_pc PLNS(plcrash_async_cfe_reader_find_pc)
#define plcrash_async_cfe_reader_free PLNS(plcrash_async_cfe_reader_free)
#define plcrash_async_cfe_reader_init PLNS(plcrash_async_cfe_reader_init)
#define plcrash_async_cfe_register_decode PLNS(plcrash_async_cfe_register_decode)
#define plcrash_async_cfe_register_encode PLNS(plcrash_async_cfe_register_encode)
#define plcrash_async_file_close PLNS(plcrash_async_file_close)
#define plcrash_async_file_flush PLNS(plcrash_async_file_flush)
#define plcrash_async_file_init PLNS(plcrash_async_file_init)
#define plcrash_async_file_write PLNS(plcrash_async_file_write)
#define plcrash_async_find_symbol PLNS(plcrash_async_find_symbol)
#define plcrash_async_image_containing_address PLNS(plcrash_async_image_containing_address)
#define plcrash_async_image_list_next PLNS(plcrash_async_image_list_next)
#define plcrash_async_image_list_set_reading PLNS(plcrash_async_image_list_set_reading)
#define plcrash_async_mach_exception_get_siginfo PLNS(plcrash_async_mach_exception_get_siginfo)
#define plcrash_async_macho_byteorder PLNS(plcrash_async_macho_byteorder)
#define plcrash_async_macho_contains_address PLNS(plcrash_async_macho_contains_address)
#define plcrash_async_macho_cpu_subtype PLNS(plcrash_async_macho_cpu_subtype)
#define plcrash_async_macho_cpu_type PLNS(plcrash_async_macho_cpu_type)
#define plcrash_async_macho_find_command PLNS(plcrash_async_macho_find_command)
#define plcrash_async_macho_find_segment_cmd PLNS(plcrash_async_macho_find_segment_cmd)
#define plcrash_async_macho_find_symbol_by_name PLNS(plcrash_async_macho_find_symbol_by_name)
#define plcrash_async_macho_find_symbol_by_pc PLNS(plcrash_async_macho_find_symbol_by_pc)
#define plcrash_async_macho_header PLNS(plcrash_async_macho_header)
#define plcrash_async_macho_header_size PLNS(plcrash_async_macho_header_size)
#define plcrash_async_macho_map_section PLNS(plcrash_async_macho_map_section)
#define plcrash_async_macho_map_segment PLNS(plcrash_async_macho_map_segment)
#define plcrash_async_macho_mapped_segment_free PLNS(plcrash_async_macho_mapped_segment_free)
#define plcrash_async_macho_next_command PLNS(plcrash_async_macho_next_command)
#define plcrash_async_macho_next_command_type PLNS(plcrash_async_macho_next_command_type)
#define plcrash_async_macho_string_free PLNS(plcrash_async_macho_string_free)
#define plcrash_async_macho_string_get_length PLNS(plcrash_async_macho_string_get_length)
#define plcrash_async_macho_string_get_pointer PLNS(plcrash_async_macho_string_get_pointer)
#define plcrash_async_macho_string_init PLNS(plcrash_async_macho_string_init)
#define plcrash_async_macho_symtab_reader_free PLNS(plcrash_async_macho_symtab_reader_free)
#define plcrash_async_macho_symtab_reader_init PLNS(plcrash_async_macho_symtab_reader_init)
#define plcrash_async_macho_symtab_reader_read PLNS(plcrash_async_macho_symtab_reader_read)
#define plcrash_async_macho_symtab_reader_symbol_name PLNS(plcrash_async_macho_symtab_reader_symbol_name)
#define plcrash_async_memcpy PLNS(plcrash_async_memcpy)
#define plcrash_async_memset PLNS(plcrash_async_memset)
#define plcrash_async_mobject_base_address PLNS(plcrash_async_mobject_base_address)
#define plcrash_async_mobject_free PLNS(plcrash_async_mobject_free)
#define plcrash_async_mobject_init PLNS(plcrash_async_mobject_init)
#define plcrash_async_mobject_length PLNS(plcrash_async_mobject_length)
#define plcrash_async_mobject_read_uint16 PLNS(plcrash_async_mobject_read_uint16)
#define plcrash_async_mobject_read_uint32 PLNS(plcrash_async_mobject_read_uint32)
#define plcrash_async_mobject_read_uint64 PLNS(plcrash_async_mobject_read_uint64)
#define plcrash_async_mobject_read_uint8 PLNS(plcrash_async_mobject_read_uint8)
#define plcrash_async_mobject_remap_address PLNS(plcrash_async_mobject_remap_address)
#define plcrash_async_mobject_task PLNS(plcrash_async_mobject_task)
#define plcrash_async_mobject_verify_local_pointer PLNS(plcrash_async_mobject_verify_local_pointer)
#define plcrash_async_objc_cache_free PLNS(plcrash_async_objc_cache_free)
#define plcrash_async_objc_cache_init PLNS(plcrash_async_objc_cache_init)
#define plcrash_async_objc_find_method PLNS(plcrash_async_objc_find_method)
#define plcrash_async_objc_supports_nonptr_isa PLNS(plcrash_async_objc_supports_nonptr_isa)
#define plcrash_async_read_addr PLNS(plcrash_async_read_addr)
#define plcrash_async_signal_sigcode PLNS(plcrash_async_signal_sigcode)
#define plcrash_async_signal_signame PLNS(plcrash_async_signal_signame)
#define plcrash_async_strcmp PLNS(plcrash_async_strcmp)
#define plcrash_async_strerror PLNS(plcrash_async_strerror)
#define plcrash_async_strncmp PLNS(plcrash_async_strncmp)
#define plcrash_async_symbol_cache_free PLNS(plcrash_async_symbol_cache_free)
#define plcrash_async_symbol_cache_init PLNS(plcrash_async_symbol_cache_init)
#define plcrash_async_task_memcpy PLNS(plcrash_async_task_memcpy)
#define plcrash_async_task_read_uint16 PLNS(plcrash_async_task_read_uint16)
#define plcrash_async_task_read_uint32 PLNS(plcrash_async_task_read_uint32)
#define plcrash_async_task_read_uint64 PLNS(plcrash_async_task_read_uint64)
#define plcrash_async_task_read_uint8 PLNS(plcrash_async_task_read_uint8)
#define plcrash_async_thread_state_clear_all_regs PLNS(plcrash_async_thread_state_clear_all_regs)
#define plcrash_async_thread_state_clear_reg PLNS(plcrash_async_thread_state_clear_reg)
#define plcrash_async_thread_state_clear_volatile_regs PLNS(plcrash_async_thread_state_clear_volatile_regs)
#define plcrash_async_thread_state_copy PLNS(plcrash_async_thread_state_copy)
#define plcrash_async_thread_state_current PLNS(plcrash_async_thread_state_current)
#define plcrash_async_thread_state_current_stub PLNS(plcrash_async_thread_state_current_stub)
#define plcrash_async_thread_state_get_greg_size PLNS(plcrash_async_thread_state_get_greg_size)
#define plcrash_async_thread_state_get_reg PLNS(plcrash_async_thread_state_get_reg)
#define plcrash_async_thread_state_get_reg_count PLNS(plcrash_async_thread_state_get_reg_count)
#define plcrash_async_thread_state_get_reg_name PLNS(plcrash_async_thread_state_get_reg_name)
#define plcrash_async_thread_state_get_stack_direction PLNS(plcrash_async_thread_state_get_stack_direction)
#define plcrash_async_thread_state_has_reg PLNS(plcrash_async_thread_state_has_reg)
#define plcrash_async_thread_state_init PLNS(plcrash_async_thread_state_init)
#define plcrash_async_thread_state_mach_thread_init PLNS(plcrash_async_thread_state_mach_thread_init)
#define plcrash_async_thread_state_map_dwarf_to_reg PLNS(plcrash_async_thread_state_map_dwarf_to_reg)
#define plcrash_async_thread_state_map_reg_to_dwarf PLNS(plcrash_async_thread_state_map_reg_to_dwarf)
#define plcrash_async_thread_state_mcontext_init PLNS(plcrash_async_thread_state_mcontext_init)
#define plcrash_async_thread_state_set_reg PLNS(plcrash_async_thread_state_set_reg)
#define plcrash_async_writen PLNS(plcrash_async_writen)
#define plcrash_log_writer_close PLNS(plcrash_log_writer_close)
#define plcrash_log_writer_free PLNS(plcrash_log_writer_free)
#define plcrash_log_writer_init PLNS(plcrash_log_writer_init)
#define plcrash_log_writer_set_exception PLNS(plcrash_log_writer_set_exception)
#define plcrash_log_writer_write PLNS(plcrash_log_writer_write)
#define plcrash_nasync_image_list_append PLNS(plcrash_nasync_image_list_append)
#define plcrash_nasync_image_list_free PLNS(plcrash_nasync_image_list_free)
#define plcrash_nasync_image_list_init PLNS(plcrash_nasync_image_list_init)
#define plcrash_nasync_image_list_remove PLNS(plcrash_nasync_image_list_remove)
#define plcrash_async_macho_free PLNS(plcrash_async_macho_free)
#define plcrash_async_macho_init PLNS(plcrash_async_macho_init)
#define plcrash_populate_error PLNS(plcrash_populate_error)
#define plcrash_populate_mach_error PLNS(plcrash_populate_mach_error)
#define plcrash_populate_posix_error PLNS(plcrash_populate_posix_error)
#define plcrash_sysctl_int PLNS(plcrash_sysctl_int)
#define plcrash_sysctl_string PLNS(plcrash_sysctl_string)
#define plcrash_sysctl_valid_utf8_bytes PLNS(plcrash_sysctl_valid_utf8_bytes)
#define plcrash_sysctl_valid_utf8_bytes_max PLNS(plcrash_sysctl_valid_utf8_bytes_max)
#define plcrash_writer_pack PLNS(plcrash_writer_pack)
#define plframe_cursor_free PLNS(plframe_cursor_free)
#define plframe_cursor_get_reg PLNS(plframe_cursor_get_reg)
#define plframe_cursor_get_regcount PLNS(plframe_cursor_get_regcount)
#define plframe_cursor_get_regname PLNS(plframe_cursor_get_regname)
#define plframe_cursor_init PLNS(plframe_cursor_init)
#define plframe_cursor_next PLNS(plframe_cursor_next)
#define plframe_cursor_next_with_readers PLNS(plframe_cursor_next_with_readers)
#define plframe_cursor_read_compact_unwind PLNS(plframe_cursor_read_compact_unwind)
#define plframe_cursor_read_dwarf_unwind PLNS(plframe_cursor_read_dwarf_unwind)
#define plframe_cursor_read_frame_ptr PLNS(plframe_cursor_read_frame_ptr)
#define plframe_cursor_thread_init PLNS(plframe_cursor_thread_init)
#define plframe_strerror PLNS(plframe_strerror)
#define plframe_test_thread_spawn PLNS(plframe_test_thread_spawn)
#define plframe_test_thread_stop PLNS(plframe_test_thread_stop)

#endif

/*
 * The following symbols are exported by the protobuf-c library. When building
 * a shared library, we can hide these as private symbols.
 *
 * However, when building a static library, we can only do so if we use
 * MH_OBJECT "single object prelink". The MH_OBJECT approach allows us to apply
 * symbol hiding/aliasing/etc similar to that supported by dylibs, but because it is
 * seemingly unused within Apple, the use thereof regularly introduces linking bugs
 * and errors in new Xcode releases.
 *
 * Rather than fighting the linker, we use the namespacing machinery to rewrite these
 * symbols, but only when explicitly compiling PLCrashReporter. Since protobuf-c is a library
 * that may be used elsewhere, we don't want to rewrite these symbols if they're used
 * independently by PLCrashReporter API clients.
 */
#ifdef PLCR_PRIVATE
   /* If no prefix has been defined, we need to specify our own private prefix */
#  ifndef PLCRASHREPORTER_PREFIX
#    define PLCRASHREPORTER_PREFIX PL_
#  endif

#  define protobuf_c_buffer_simple_append                   PLNS(protobuf_c_buffer_simple_append)
#  define protobuf_c_default_allocator                      PLNS(protobuf_c_default_allocator)
#  define protobuf_c_enum_descriptor_get_value              PLNS(protobuf_c_enum_descriptor_get_value)
#  define protobuf_c_enum_descriptor_get_value_by_name      PLNS(protobuf_c_enum_descriptor_get_value_by_name)
#  define protobuf_c_message_descriptor_get_field           PLNS(protobuf_c_message_descriptor_get_field)
#  define protobuf_c_message_descriptor_get_field_by_name   PLNS(protobuf_c_message_descriptor_get_field_by_name)
#  define protobuf_c_message_free_unpacked                  PLNS(protobuf_c_message_free_unpacked)
#  define protobuf_c_message_get_packed_size                PLNS(protobuf_c_message_get_packed_size)
#  define protobuf_c_message_pack                           PLNS(protobuf_c_message_pack)
#  define protobuf_c_message_pack_to_buffer                 PLNS(protobuf_c_message_pack_to_buffer)
#  define protobuf_c_message_unpack                         PLNS(protobuf_c_message_unpack)
#  define protobuf_c_out_of_memory                          PLNS(protobuf_c_out_of_memory)
#  define protobuf_c_service_descriptor_get_method_by_name  PLNS(protobuf_c_service_descriptor_get_method_by_name)
#  define protobuf_c_service_destroy                        PLNS(protobuf_c_service_destroy)
#  define protobuf_c_service_generated_init                 PLNS(protobuf_c_service_generated_init)
#  define protobuf_c_system_allocator                       PLNS(protobuf_c_system_allocator)
#endif /* PLCR_PRIVATE */
