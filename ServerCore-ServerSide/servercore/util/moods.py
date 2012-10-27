'''
Created on Oct 26, 2012

@author: hunlan
'''
class Moods:
    HUMOROUS=0
    ENERVATE=1
    ROMANTIC=2
    INSPIRE=3
    all = [HUMOROUS, ENERVATE, ROMANTIC, INSPIRE]
    
    @staticmethod
    def name():
        return 'mood'