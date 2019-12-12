# About
This was the programming assignement of the Discrete Structures course at KFUPM.

The idea is to read the relation matrix from a *.txt* file and analyze it to check its properites.

## This code is a mess, i know!
That`s what happens when i try to code something without first really studying it.
I took the general idea of everything and got to work. What i did in 300 lines for the Hasse Diagram can be accomplished in WAY less. I actually forgot that i even have a matrix that i can process in much simpler ways. Instead, i wrote a structure to store the matrix in THEN process the stucture rather than the matrix, which resulted in an absolute mess. Tryin to solve any problem just complicated it further, so yeah xD.

# HOW TO
- It takes a *.txt* file with the following structure:
    3                    <- *number of elements*
    1 2 3                <- *elements sperated by a space*
                         <- *empty line*
    0 1 0                <- *the matrix*
    1 1 0
    1 0 1
- Provide the absolute path to the files in the constructor.
    