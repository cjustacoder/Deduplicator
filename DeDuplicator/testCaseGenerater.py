import random
import os
txts = []
os.makedirs("./tests")

for i in range(50):
    txts.append("./tests/test"+str(i)+".txt")

for i in range(50):
    ra = random.randint(0, 7000000)
    with open(txts[i], "w") as f:

        for i in range(0, ra):
            f.write(chr(i % 256))
        for i in range(ra, 7000000):
            f.write(chr(i % 256))
    f.close()

