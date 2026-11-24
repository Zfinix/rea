import { z } from "zod";

/** One C declaration (struct, union, enum or typedef) to add to the program's types. */
export const nativeCDeclarationSchema = z.string().min(1).max(20_000);

/** Readback after a C type is added, and placed at an address when one was given. */
export const nativeCTypeResultSchema = z.strictObject({
  name: z.string().min(1),
  size: z.number().int(),
  address: z.string().nullable(),
  unit: z.string().nullable(),
});
