#include <iostream>
#include <fstream>
#include <string>

using namespace std;

int calculateChecksum(const string& str, int length)
{
    int checksum = 0;
    for(int i = 0; i < length; i++)
        checksum += (int)str[i];
    return checksum;
}

void searchPattern(const string& pattern, const string& text)
{
    int patternLength = pattern.length();
    int textLength = text.length();
    int patternChecksum = calculateChecksum(pattern, patternLength);
    int textChecksum = calculateChecksum(text, patternLength);

    for(int i = 0; i <= textLength - patternLength; i++)
    {
        if(patternChecksum == textChecksum)
        {
            int j;
            for(j = 0; j < patternLength; j++)
            {
                if(text[i + j] != pattern[j])
                {
                    j = patternLength + 1;
                    break;
                }
            }

            if(j == patternLength)
                cout << i << " ";
        }

        if(i < textLength - patternLength)
            textChecksum = textChecksum - (int)text[i] + (int)text[i + patternLength];
    }
}

int main()
{
    string pattern, text, file;
    int N;
    cin >> N;
    for(int i = 0; i < N; i++)
    {
        cin >> file;
        fstream IN(file);
        IN >> text;
        IN.close();
        cin >> pattern;
        searchPattern(pattern, text);
        cout << endl;
    }
    return 0;
}
