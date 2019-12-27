//Praneeth Sangani (PRS79)
public class MyLZW {

    private static final int R = 256;                                                       // number of input chars
    private static int L = 512;                                                             // number of codewords = 2^W
    private static int W = 9;                                                               // codeword width
    private static char mode;                                                               // The mode to compress/expand with
    private static final int MAX_CODEWORD_WIDTH = 16;                                       // Max codeword width - goes from 9 to 16
    private static final int MAX_NUM_OF_CODEWORDS = (int) Math.pow(2,MAX_CODEWORD_WIDTH);   // Max number of codeword 2^16

    public static void main(String[] args)
    {
        if (args[0].equals("-") && argumentsAreValid(args))
        {
            mode = args[1].charAt(0);
            compress();
        }
        else if (args[0].equals("+")) expand();
        else throw new IllegalArgumentException("Illegal command line argument");
    }

    // Checks if the user entered a proper mode to compress with
    private static boolean argumentsAreValid(String[] arg)
    {
        return arg[1].charAt(0) == 'n' || arg[1].charAt(0) == 'r' || arg[1].charAt(0) == 'm';
    }

    // Compress a file
    public static void compress()
    {
        double uncompressedSize = 0;
        double compressedSize = 0;
        double oldRatio = 0;

        BinaryStdOut.write(mode);                               // Add the compression mode to the file, so that it can be expanded the same way

        String input = BinaryStdIn.readString();
        TST<Integer> st = new TST<>();
        for (int i = 0; i < R; i++)
            st.put("" + (char) i, i);
        int code = R + 1; // R is codeword for EOF

        while (input.length() > 0)
        {
            String s = st.longestPrefixOf(input);               // Find max prefix match s.
            BinaryStdOut.write(st.get(s), W);                   // Print s's encoding.
            int t = s.length();

            uncompressedSize += t * 8;
            compressedSize += W;

            if (t < input.length() && code < L)                 // Add s to symbol table.
            {
                st.put(input.substring(0, t + 1), code++);
                oldRatio = uncompressedSize / compressedSize;
            }
            else if (shouldIncreaseCodeWordWidth(code))         // Increments the codeword width from 9 up to 16 and L respectively.
            {
                W++;
                L = (int) Math.pow(2, W);
                st.put(input.substring(0, t + 1), code++);
            }
            else if (resetMode(mode, code))                     // If the user chose reset mode, resets dictionary back to initial state after it is full
            {
                st = compressReset();
                W = 9; L = 512; code = R + 1;
                st.put(input.substring(0, t + 1), code++);
            }
            else if (monitorMode(mode, code))                   // If the user chose monitor mode, reset the dictionary when the compression ratio is greater than 1.1
            {
                double compressionRatio = calculateCompressionRatio(uncompressedSize, compressedSize, oldRatio);

                if (compressionRatio > 1.1)
                {
                   st = compressReset();
                   W = 9; L = 512; code = R + 1;
                   st.put(input.substring(0, t + 1), code++);
                }
            }
            input = input.substring(t);                     // Scan past s in input.
        }
        BinaryStdOut.write(R, W);
        BinaryStdOut.close();
    }

    // Resets the dictionary
    private static TST<Integer> compressReset()
    {
        TST<Integer> st = new TST<>();
        for (int i = 0; i < R; i++)
            st.put("" + (char) i, i);
        return st;
    }

    // Expand a file compressed by this program
    public static void expand()
    {
        double uncompressedSize = 0;
        double compressedSize = 0;
        double oldRatio = 0;

        mode = BinaryStdIn.readChar();                              // Read what mode the file was compressed with

        String[] st = new String[MAX_NUM_OF_CODEWORDS];
        int i;

        // initialize symbol table with all 1-character strings
        for (i = 0; i < R; i++)
            st[i] = "" + (char) i;
        st[i++] = ""; // (unused) lookahead for EOF

        i = R + 1;

        int codeword = BinaryStdIn.readInt(W);
        if (codeword == R) return; // expanded message is empty string
        String val = st[codeword];

        while (true)
        {
            uncompressedSize += (val.length() * 8);
            compressedSize += W;

            BinaryStdOut.write(val);
            if (shouldIncreaseCodeWordWidth(i))                     // Increments the codeword width from 9 up to 16 and L respectively.
            {
                W++;
                L = (int) Math.pow(2, W);
                oldRatio = uncompressedSize / compressedSize;
            }
            else if (resetMode(mode, i))                            // If the user chose reset mode, resets dictionary back to initial state after it is full
            {
                st = expandReset(i);
                W = 9; L = 512; i = R + 1;
            }
            else if (monitorMode(mode, i))                          // If the user chose monitor mode, reset the dictionary when the compression ratio is greater than 1.1
            {
                double compressionRatio = calculateCompressionRatio(uncompressedSize, compressedSize, oldRatio);

                if (compressionRatio > 1.1)
                {
                    st = expandReset(i);
                    W = 9; L = 512; i = R + 1; oldRatio = 0;
                }
            }
            codeword = BinaryStdIn.readInt(W);
            if (codeword == R) break;
            String s = st[codeword];
            if (i == codeword) s = val + val.charAt(0);   // special case hack
            if (i < L) {
                st[i++] = val + s.charAt(0);
                oldRatio = uncompressedSize / compressedSize;
            }
            val = s;
        }
        BinaryStdOut.close();
    }

    // Resets the dictionary
    private static String[] expandReset(int i) 
    {
        String[] st = new String[MAX_NUM_OF_CODEWORDS];
        // initialize symbol table with all 1-character strings
        for (i = 0; i < R; i++)
            st[i] = "" + (char) i;
        st[i++] = ""; // (unused) lookahead for EOF
        return st;
    }

    // Calculates the compression ratio, which is used to check if we should reset the dictionary
    private static double calculateCompressionRatio(double uncompressedSize, double compressedSize, double oldRatio)
    {
        double currRatio = uncompressedSize / compressedSize;
        return oldRatio / currRatio;
    }

    private static boolean shouldIncreaseCodeWordWidth(int code) 
    {
        return W < MAX_CODEWORD_WIDTH && code == L;
    }

    private static boolean resetMode(char mode, int i)
    {
        return mode == 'r' && i == MAX_NUM_OF_CODEWORDS;
    }

    private static boolean monitorMode(char mode, int i)
    {
        return mode == 'm' && i == MAX_NUM_OF_CODEWORDS;
    }
}