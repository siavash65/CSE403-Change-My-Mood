'''
Created on Oct 26, 2012

@author: hunlan
'''
class Contents:
    PICTURE=0
    VIDEO=1
    TEXT=2
    MUSIC=3
    all = [PICTURE, VIDEO, TEXT, MUSIC]
    
    @staticmethod
    def name():
        return 'content'