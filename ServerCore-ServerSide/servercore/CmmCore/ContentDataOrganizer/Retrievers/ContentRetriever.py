'''
Created on Nov 17, 2012

@author: hunlan
'''

PRIME_SCORE = 20
SECOND_SCORE = 5

FIRST_VIEW_HURDLE = 5000
SECOND_VIEW_HURDLE = 10000
THIRD_VIEW_HURDLE = 100000

VIEW_SCORE_ONE = 10
VIEW_SCORE_TWO = 10
VIEW_SCORE_THREE = 5

FIRST_COMMENT_HURDLE = 10
SECOND_COMMENT_HURDLE = 50

COMMENT_SCORE_ONE = 5
COMMENT_SCORE_TWO = 5

FIRST_FAV_HURDLE= 50
SECOND_FAV_HURDLE = 100
THIRD_FAV_HURDLE = 1000

FAV_SCORE_ONE = 10
FAV_SCORE_TWO = 10
FAV_SCORE_THREE = 5


def computeInitialScore(isPrimeMatch=False, numSecond=0, \
                        views=0, comments=0, favs=0):
    score = 0
    if isPrimeMatch :
        score += PRIME_SCORE
        
    multi = numSecond if numSecond < 4 else 4
    score += multi * SECOND_SCORE
    
    if views > FIRST_VIEW_HURDLE:
        score += VIEW_SCORE_ONE
        
        if views > SECOND_VIEW_HURDLE:
            score += VIEW_SCORE_TWO
            
            if views > THIRD_VIEW_HURDLE:
                score += VIEW_SCORE_THREE
     
    if comments > FIRST_COMMENT_HURDLE:
        score += COMMENT_SCORE_ONE
        
        if comments > SECOND_COMMENT_HURDLE:
            score += COMMENT_SCORE_TWO
     
    if views > FIRST_FAV_HURDLE:
        score += FAV_SCORE_ONE
        
        if views > SECOND_FAV_HURDLE:
            score += FAV_SCORE_TWO
            
            if views > THIRD_FAV_HURDLE:
                score += FAV_SCORE_THREE           

    assert score <= 100
    return score
        