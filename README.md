class HuffmanEncoderDecoder-
The Compress function accepts two arrays - an array of input files and an array of output files. Then calls the function countTheChar-function constructs a map using the Java map library. When the key is the character entered from the file and the value is the number of times it appears in the file.
Then called the buildTree function - this function builds the Huffman tree as a priority tree (with the priority queue), adding the information on the input we collected earlier to the map.
At this point we will begin to write to the file, first of all the encoding of each letter, so that when someone else opens the file he will know how to decode it.
Then, the function passes through the file again, reads the chracters one by one, converts it to Huffman encoding (using the map we built in charToCodeMap), calls the writebit function which inserts 8 characters into bytes and then writes the bytes to the output file.

The decompress function accepts two arrays - an array of input files and an array of output files. First of all, the function reads the encoding of each letter we wrote to in the compress phase. Then, you read a character from the file - when a character is 0 it goes to the left in the tree, where it is 1, right until it reaches a vertex with a character (as opposed to the null-null character) The output files are in the 1st place so the output directory must be prepared in the output_names array instead of the 1).
readbit function- With this function , we can take a character from the bit that we have received and add it to encoding - and so look for which letter belongs to the encoding we started looking for.

Notes: We used string to represent the character rather than char so that it would be appropriate for us to encode more than one character, such as the following function, including other functions that can be added to look for sequences.
