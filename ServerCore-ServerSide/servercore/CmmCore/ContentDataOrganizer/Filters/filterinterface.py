'''
Created on Oct 31, 2012

@author: hunlan
'''
class FilterInterface( object ):
    
    '''
    filter our database
    '''
    @staticmethod
    def filter(mood, content, delete_ratio):
        raise NotImplementedError( "Should have implemented this" )
        