# LZW Compression
1. **Do Nothing mode** Do nothing and continue to use the full codebook (this is the mode implemented by LZW.java).
2. **Reset mode** Reset the dictionary back to its initial state so that new codewords can be added. Be careful to reset at the appropriate place for both compression and expansion, so that the algorithms remain in sync. This is very tricky and may require alot of planning in order to get it working correctly.
3. **Monitor mode** Initially do nothing (keep using the full codebook) but begin monitoring thecompression ratio whenever you fill the codebook. Define the compression ratio to be the size ofthe uncompressed data that has been processed/generated so far divided by the size of the compressed data generated/processed so far (for compression/expansion, respectively). If the compression ratio degrades by more than a set threshold from the point when the last codewordwas added, then reset the dictionary back to its initial state. To determine the threshold for resetting you will take a ratio of compression ratios [(old ratio)/(new ratio)], where old ratio is the ratio recorded when your program last filled the codebook, and new ratio is the currentcompression ratio. If the "ratio of ratios" exceeds 1.1, then you should reset.For example, if the compression ratio when you start monitoring is 2.5 and the compression ratio at some later point is 2.3, the ratio of ratios at that point would be 2.5/2.3 = 1.087, so you shouldnot reset the dictionary. Continuing, if your compression ratio drops to 2.2, the ratio of ratioswould become 2.5/2.2 or 1.136. This means that your ratio of ratios has exceeded the thresholdof 1.1 and you should now reset the dictionary. Be very careful to coordinate the code for both compression and expansion so that it works correctly.
# Command line args
- n for Do Nothing mode
- r for Reset mode
- m for Monitor mode 

This was a project for cs1501 at the University of Pittsburgh
