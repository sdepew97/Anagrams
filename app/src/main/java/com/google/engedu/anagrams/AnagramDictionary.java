/* Copyright 2016 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.engedu.anagrams;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

public class AnagramDictionary {

    private static final int MIN_NUM_ANAGRAMS = 1;
    private static final int DEFAULT_WORD_LENGTH = 3;
    private static final int MAX_WORD_LENGTH = 7;
    private static final int NUM_CHARS = 256;
    private ArrayList<String> wordList = new ArrayList<>();
    private HashSet<String> wordSet = new HashSet<>();
    private HashMap<String, ArrayList<String>> lettersToWord = new HashMap<>();
    private HashMap<Integer, ArrayList<String>> sizeToWords = new HashMap<>();
    private static int wordLength = DEFAULT_WORD_LENGTH;

    public AnagramDictionary(Reader reader) throws IOException
    {
        BufferedReader in = new BufferedReader(reader);
        String line;
        while((line = in.readLine()) != null)
        {
            String word = line.trim();

            //Want to add the word to the hash set wordSet
            wordList.add(word);
            wordSet.add(word);

            String sorted = sortLetters(word);

            //Sorted version of word already in Hash Map
            if(lettersToWord.containsKey(sorted))
            {
                ArrayList<String> updatedList = lettersToWord.get(sorted);
                updatedList.add(word);
                lettersToWord.put(sorted, updatedList);
            }
            else
            {
                ArrayList<String> newList = new ArrayList<>();
                newList.add(word);
                lettersToWord.put(sorted, newList);
            }

            //Fill sizeToWords
            if(sizeToWords.containsKey(sorted.length()))
            {
                ArrayList<String> updatedList = sizeToWords.get(sorted.length());
                updatedList.add(word);
                sizeToWords.put(sorted.length(), updatedList);
            }
            else
            {
                ArrayList<String> newList = new ArrayList<>();
                newList.add(word);
                sizeToWords.put(sorted.length(), newList);
            }
        }




     /*  for (int i=0; i<sizeToWords.size(); i++)
        {
            if(sizeToWords.containsKey(i))
            {
                Log.v("list of size", sizeToWords.get(i).toString());
            }
        }*/
    }

    public boolean isGoodWord(String word, String base)
    {
        Log.d("word", word);
        Log.d("base", base);
        return (wordSet.contains(word) && !word.contains(base));
    }

    public List<String> getAnagrams(String targetWord)
    {
        ArrayList<String> result = new ArrayList<String>();

        String sortedTargetWord = sortLetters(targetWord);
        String sortedWordInList;

        for (String word : wordList)
        {
            sortedWordInList = sortLetters(word);
            if(sortedTargetWord.length()==sortedWordInList.length() && sortedTargetWord.equals(sortedWordInList))
            {
                //Add word to the result list, since it is an anagram
                result.add(word);
            }
        }

        return result;
    }

    private String sortLetters(String string)
    {
        String sortedString = new String();
        int[] charsCount = new int[NUM_CHARS];
        char[] stringChars = string.toCharArray();

        //Sort letters of word here
        for (char c : stringChars)
        {
            charsCount[c - 'a']++;
        }

        //Build the string to output
        for(int i=0; i<charsCount.length; i++)
        {
            for(int j=0; j<charsCount[i]; j++)
            {
                char c = (char)('a' + i);
                sortedString += c;
            }
        }

        return sortedString;
    }

    public List<String> getAnagramsWithOneMoreLetter(String base)
    {
        ArrayList<String> result = new ArrayList<>();
        String stringToCheck;

        for(int i='a'; i<='z'; i++)
        {
            stringToCheck = sortLetters(base + (char) i);

            if(lettersToWord.containsKey(stringToCheck))
            {
                for (String word:lettersToWord.get(stringToCheck))
                {
                  if(isGoodWord(word, base))
                  {
                      result.add(word);
                  }
                }
            }
        }

        return result;
    }

    /*
    If your game is working, proceed to implement pickGoodStarterWord to make the game more interesting.
    Pick a random starting point in the wordList array and check each word in the array until you find one that has at least MIN_NUM_ANAGRAMS anagrams.
    Be sure to handle wrapping around to the start of the array if needed.
    */

    public String pickGoodStarterWord()
    {
        //get ArrayList of eligible words
        ArrayList<String> eligibleWords = sizeToWords.get(wordLength);

        Random random = new Random();
        Integer randomInt = random.nextInt(eligibleWords.size());

        boolean foundValue = false;
        String starterWord = null;

        Log.d("Eligible word", eligibleWords.get(randomInt));
        Log.d("sort of eligible", sortLetters(eligibleWords.get(randomInt)));
        Log.d("getting sorted value", lettersToWord.get(sortLetters(eligibleWords.get(randomInt))).toString());
        while(!foundValue)
        {
            String eligibleWord = eligibleWords.get(randomInt);
            String sortedEligibleWord = sortLetters(eligibleWord);

            if(lettersToWord.get(sortedEligibleWord).size()>=MIN_NUM_ANAGRAMS)
            {
                foundValue = true;
                if(this.wordLength<MAX_WORD_LENGTH) {
                    this.wordLength++;
                }

                starterWord = eligibleWord;
            }
            else
            {
                //Account for wrapping around, here
                randomInt++;
                randomInt = randomInt%(eligibleWords.size());
            }
        }

        return starterWord;
    }
}
