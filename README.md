# Assignment 4. Patient Zero

Student ID: 252373
Name: Sanzhar Zhunis
Group: SE-2519

This file is the report. Fill it in as you go. How to start: `START_HERE.md`. The task: `docs/Assignment4.pdf`.

## Program output

The 9 lines your program prints on your data (`java -cp out Main`):

    Mutations: 83
    Shared core: 947
    Marker hits: 5
    First hit: 2162
    Border length: 4
    Shared words: 380
    Tests needed: 27
    Cheapest testing: 112
    Longest chain: 21

My outbreak report (a screenshot of `output/outbreak.html`):

![outbreak](output/outbreak.png)

## Questions

Answer in your own words, two or three sentences each.

Mission 1. What does the cell d[i][j] of your table mean, and how is it computed from other cells?

d[i][j] is the smallest number of changes (replace, insert, delete) needed to turn the first i letters of A into the first j letters of B. It is computed from three smaller cells. The diagonal d[i-1][j-1] plus 0 if the two letters are equal or plus 1 if I replace one, the cell above d[i-1][j] plus 1 (delete a letter of A), and the cell on the left d[i][j-1] plus 1 (insert a letter of B). I take the smallest of the three. The first row is 0, 1, 2, ... (only insertions) and the first column is 0, 1, 2, ... (only deletions).

Mission 2. How is the longest common subsequence different from the longest common substring? My Shared core is bigger than the number of letters that are the same at the same place: why?

A common substring must be a block of letters that stand next to each other in both strains, though a common subsequence can skip letters, it only needs the same order. B was made from A by replacing, inserting and deleting letters, so after an insert or a delete everything after it moves by 1 place. Because of this, many letters are = in A and B but stand at different positions, and the subsequence can still count them. That is why my Shared core (947) is much bigger than the number of letters that are equal at the same position.

Mission 3. What does the prefix function tell you, and why does the pointer in the text never go back in KMP? What is the Border length of my marker and what does it mean?

The prefix function pi[i] is the length of the longest beginning of the marker (a proper prefix of its first i+1 letters) that is also an ending of those letters. KMP uses it on a mismatch. the letters I just read already match the first k letters of the marker, and pi tells me how much of that is still useful, so I only move the marker pointer k back to pi[k-1] and the pointer in the text only goes forward. After a full match I also go to pi[m-1], so overlapping matches are found. The Border length of my marker is 4: the first 4 letters of the marker are the same as its last 4 letters, so two occurrences can overlap by up to 4 letters.

Mission 4. Why must you compare the letters after the hashes are equal? What happens to the cost of Rabin-Karp if many windows have the same hash?

There are only MOD different hash values but many more possible words, so two different words can get the same hash, which is a collision. Equal hash only means possibly equal, so I compare the letters before I count, otherwise I would count false matches. And if many windows have the same hash, every lookup has to compare many windows letter by letter, and the time can grow from about O(n) to O(n * m * k).

Mission 5. What are the two states of a person in your DP, and how do you get the answer for a person from its children? Why does a greedy "test the person with most links first" not always give the cheapest answer?

For every person v (looking at v and everything below it) I keep in[v], the cheapest cost when v is tested, and out[v], the cheapest cost when v is not tested. in[v] = cost[v] + the sum over children c of min(in[c], out[c]), because if v is tested the children are free to choose. out[v] = the sum over children c of in[c], because if v is not tested every child must be tested to cover its link. The answer is min(in[0], out[0]) at the root, and I walk down from the root with the same rule to see who is tested. Greedy fails: take a person x with 3 neighbours y1, y2, y3, and each y has one more leaf friend. Greedy tests x first (most links) and then still needs all 3 of the y's, so 4 tests, but testing only y1, y2, y3 covers every link with 3 tests. Greedy also ignores the costs, which the DP uses.

## AI use

I used Gemini as a helper. I asked it to explain the concepts in this assignment. No code or text was taken from a book or a website.

## Timing (Mission 7)

Milliseconds, average of 3 runs, from `java -cp out Bench`.

| test | size | time |
|---|---|---|
| naive search | 20 000 / 40 000 / 80 000 | 31.2 / 76.5 / 159.7 |
| KMP | 20 000 / 40 000 / 80 000 | 1.9 / 1.1 / 0.6 |
| edit distance | 500 / 1000 / 2000 / 4000 | 7.4 / 9.6 / 29.3 / 114.9 |

What do the numbers say about how the time grows? When you double the size, by how much does each time grow? Two or three sentences.

The naive search grows about 2 times when the text doubles (31 to 77 to 160 ms), so it is linear in the text, but it is slow because on this input it compares up to 2000 letters at every position (about n * m work). KMP stays at 1 to 2 ms for all sizes because it reads every letter of the text once, and the small differences are just noise from the timer and the JVM warming up. Edit distance grows about 4 times when the length doubles (29 to 115 ms from 2000 to 4000), which matches O(n * m) with n = m. The first steps (7.4 to 9.6 ms) grow less because for small sizes the JVM warm-up is a big part of the time.

## Complexity

n is the length of the text (or of a strain), m the length of the pattern, k the length of the window, v the number of people. Fill in every algorithm you wrote.

| algorithm | time | space | assumptions |
|---|---|---|---|
| Edit distance | O(n * m) | O(n * m) | Two strains of length n and m. Two nested loops fill the whole table of (n+1) x (m+1) cells, each cell in O(1). Keeping only two rows would give O(m) space, but I keep the full table. |
| LCS | O(n * m) | O(n * m) | Same table and the same loops as the edit distance, each cell is a comparison and a max or a +1. |
| Prefix function and KMP | O(n + m) | O(n + m) | n = text, m = marker. The prefix function is O(m) because k goes back at most as many times as it went forward. The search is O(n) for the same reason. Space: pi is O(m), my result buffer is O(n) (O(number of hits) would be possible). |
| Rabin-Karp (shared words) | O(n + m + k) expected, O(n * m * k) worst case | O(m) | n = length of A, m = length of B, k = window (12). Rolling hash costs O(1) per window. Letters are compared only when hashes are equal, so a true match costs O(k). Worst case: all windows have the same hash and every lookup compares many windows. The map keeps the hashes of the windows of B. |
| Tree DP | O(v) | O(v) | v people, v-1 links. BFS to get the order and the parents is O(v), the two passes (up and down) are O(v), the longest chain is also one pass. Arrays in, out, parent, order, best1, best2 are all of size v. |

## Before you submit

- The four files in `data` and the screenshot `output/outbreak.png` are in the repository.
- `java tools/Check.java` shows all examples right.
- This README is filled in, the program output and the section AI use included.
- The work is pushed to main (at least one commit per mission) and the repository is public.
- The link is attached in Moodle.
