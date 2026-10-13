import { z } from "zod";

/** Requested listing kind; "data" cycles byte, word, dword, qword on repeat. */
export const NATIVE_CODE_TYPES = [
  "code",
  "procedure",
  "data",
  "byte",
  "word",
  "dword",
  "qword",
  "cstring",
  "undefined",
] as const;

export const nativeCodeTypeSchema = z.enum(NATIVE_CODE_TYPES);

/** Readback after one listing change; `type` is the resolved kind applied. */
export const nativeCodeTypeResultSchema = z.strictObject({
  address: z.string().min(1),
  type: nativeCodeTypeSchema.exclude(["data"]),
  unit: z.string().nullable(),
  function: z.string().min(1).nullable(),
});
